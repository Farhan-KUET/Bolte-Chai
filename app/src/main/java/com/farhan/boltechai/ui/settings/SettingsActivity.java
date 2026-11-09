package com.farhan.boltechai.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.firebase.FirebaseSyncManager;
import com.farhan.boltechai.databinding.ActivitySettingsBinding;
import com.farhan.boltechai.ui.auth.AuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * SettingsActivity — Allows changing language, grid size, scanning mode, caregiver PIN, and data clearing.
 *
 * Lab Topic: SharedPreferences, Room database, Firebase
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            firebaseAuth = FirebaseAuth.getInstance();
        } catch (Exception ignored) {}

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }

        setupLanguageToggle();
        setupGridSizeSelector();
        setupScanningMode();
        setupCaregiverPin();
        setupFirebaseSection();
        setupDeleteData();
    }

    private void setupLanguageToggle() {
        boolean isEnglish = "en".equals(BolteChaiApp.getInstance().getCurrentLanguage());
        binding.switchLanguage.setChecked(isEnglish);
        binding.switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BolteChaiApp.getInstance().toggleLanguage();
            recreate();
        });
    }

    private void setupGridSizeSelector() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grid_size_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGridSize.setAdapter(adapter);

        int currentCols = BolteChaiApp.getInstance().getGridSize();
        int position = currentCols - 2;
        if (position >= 0 && position < adapter.getCount()) {
            binding.spinnerGridSize.setSelection(position);
        }

        binding.spinnerGridSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int columns = pos + 2;
                BolteChaiApp.getInstance().setGridSize(columns);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupScanningMode() {
        binding.switchScanningMode.setChecked(BolteChaiApp.getInstance().isScanningEnabled());
        binding.switchScanningMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BolteChaiApp.getInstance().setScanningEnabled(isChecked);
        });

        int currentSpeed = BolteChaiApp.getInstance().getScanningSpeed();
        int progress = (currentSpeed - 500) / 450;
        binding.seekbarScanningSpeed.setProgress(Math.max(0, Math.min(10, progress)));

        binding.seekbarScanningSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int speed = 500 + (progress * 450);
                BolteChaiApp.getInstance().setScanningSpeed(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupCaregiverPin() {
        String pin = BolteChaiApp.getInstance().getCaregiverPin();
        binding.editPin.setText(pin);
        binding.editPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BolteChaiApp.getInstance().setCaregiverPin(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFirebaseSection() {
        FirebaseUser user = FirebaseSyncManager.getInstance().getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            binding.textFirebaseAccount.setText(getString(R.string.firebase_account) + ": " + user.getEmail());
            binding.btnAuthAction.setText(R.string.auth_signout);
            binding.btnAuthAction.setOnClickListener(v -> {
                FirebaseSyncManager.getInstance().signOut();
                Toast.makeText(this, R.string.auth_signout, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        } else {
            binding.textFirebaseAccount.setText(getString(R.string.firebase_account) + ": " + getString(R.string.firebase_anonymous));
            binding.btnAuthAction.setText(R.string.auth_login_btn);
            binding.btnAuthAction.setOnClickListener(v -> {
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
            });
        }

        binding.btnSyncNow.setOnClickListener(v -> {
            FirebaseSyncManager.getInstance().syncBoards(this);
            Toast.makeText(this, R.string.auth_sync_success, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDeleteData() {
        binding.btnDeleteData.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_data_title)
                    .setMessage(R.string.delete_data_message)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        new Thread(() -> {
                            AppDatabase.getInstance(this).clearAllTables();
                            runOnUiThread(() -> {
                                Toast.makeText(this, R.string.ui_delete_all_data, Toast.LENGTH_SHORT).show();
                            });
                        }).start();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
