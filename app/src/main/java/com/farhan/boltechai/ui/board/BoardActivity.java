package com.farhan.boltechai.ui.board;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.db.entity.TileEntity;
import com.farhan.boltechai.data.db.entity.UsageEntity;
import com.farhan.boltechai.databinding.ActivityBoardBinding;
import com.farhan.boltechai.service.SpeechService;
import com.farhan.boltechai.ui.adapter.TileAdapter;
import com.farhan.boltechai.ui.editor.TileEditorActivity;
import com.farhan.boltechai.ui.scanning.ScanningHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * BoardActivity — Grid of communication tiles for a specific category.
 *
 * Lab Topic: Room database, RecyclerView, Bound Service, Scanning Mode, Explicit/Implicit Intent
 */
public class BoardActivity extends AppCompatActivity {

    private ActivityBoardBinding binding;
    private String categoryKey = AppConstants.CAT_FOOD;
    private String categoryName = "";

    private SpeechService speechService;
    private boolean isBound = false;
    private TileAdapter tileAdapter;
    private ScanningHelper scanningHelper;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SpeechService.LocalBinder binder = (SpeechService.LocalBinder) service;
            speechService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConstants.EXTRA_CATEGORY_KEY)) {
                categoryKey = intent.getStringExtra(AppConstants.EXTRA_CATEGORY_KEY);
            }
            if (intent.hasExtra(AppConstants.EXTRA_CATEGORY_NAME)) {
                categoryName = intent.getStringExtra(AppConstants.EXTRA_CATEGORY_NAME);
            }
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName.isEmpty() ? getString(R.string.app_name) : categoryName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int columns = BolteChaiApp.getInstance().getGridSize();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        binding.sentenceStripView.setOnSpeakClickListener(sentence -> {
            if (isBound && speechService != null) {
                speechService.speak(sentence);
            } else {
                Toast.makeText(this, R.string.service_not_bound, Toast.LENGTH_SHORT).show();
            }
        });

        binding.fabAddTile.setOnClickListener(v -> checkPinAndOpenEditor());

        loadTiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTiles();
    }

    private void loadTiles() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<TileEntity> tiles = db.tileDao().getTilesByCategory(categoryKey);
            runOnUiThread(() -> {
                String lang = BolteChaiApp.getInstance().getCurrentLanguage();
                tileAdapter = new TileAdapter(this, tiles, lang, (tile, position) -> {
                    onTileSelected(tile);
                });
                binding.recyclerView.setAdapter(tileAdapter);

                if (BolteChaiApp.getInstance().isScanningEnabled()) {
                    if (scanningHelper != null) {
                        scanningHelper.stopScanning();
                    }
                    int speed = BolteChaiApp.getInstance().getScanningSpeed();
                    scanningHelper = new ScanningHelper(tileAdapter, speed);
                    scanningHelper.startScanning();
                }
            });
        }).start();
    }

    private void onTileSelected(TileEntity tile) {
        binding.sentenceStripView.addTile(tile);

        // Lab Topic: Room database — record usage frequency
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            UsageEntity existing = db.usageDao().getUsageForTile(tile.getId());
            if (existing != null) {
                existing.setUseCount(existing.getUseCount() + 1);
                existing.setLastUsed(System.currentTimeMillis());
                db.usageDao().insertOrUpdate(existing);
            } else {
                UsageEntity newUsage = new UsageEntity(
                        tile.getId(),
                        1,
                        System.currentTimeMillis(),
                        tile.getLabelBn(),
                        tile.getLabelEn(),
                        tile.getCategory()
                );
                db.usageDao().insertOrUpdate(newUsage);
            }
        }).start();
    }

    private void checkPinAndOpenEditor() {
        String savedPin = BolteChaiApp.getInstance().getCaregiverPin();
        if (savedPin == null || savedPin.isEmpty()) {
            openTileEditor();
            return;
        }

        // Show PIN Dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pin, null);
        EditText pinInput = dialogView.findViewById(R.id.et_pin);

        new AlertDialog.Builder(this)
                .setTitle(R.string.enter_pin)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, w) -> {
                    String entered = pinInput.getText().toString().trim();
                    if (savedPin.equals(entered)) {
                        openTileEditor();
                    } else {
                        Toast.makeText(this, "PIN Incorrect", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void openTileEditor() {
        Intent intent = new Intent(this, TileEditorActivity.class);
        intent.putExtra(AppConstants.EXTRA_CATEGORY_KEY, categoryKey);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Scanning mode switch access via volume key
        if (BolteChaiApp.getInstance().isScanningEnabled() && scanningHelper != null) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                TileEntity selected = scanningHelper.selectCurrent();
                if (selected != null) {
                    onTileSelected(selected);
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            String sentence = binding.sentenceStripView.getSentenceText();
            if (!sentence.isEmpty()) {
                com.farhan.boltechai.ui.util.ShareHelper.shareSentence(this, sentence);
            } else {
                com.farhan.boltechai.ui.util.ShareHelper.shareViewAsImage(this, binding.getRoot(), categoryName);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SpeechService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (scanningHelper != null) {
            scanningHelper.stopScanning();
        }
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
