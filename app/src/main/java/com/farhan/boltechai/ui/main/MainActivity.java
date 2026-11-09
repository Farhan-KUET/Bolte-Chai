package com.farhan.boltechai.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.databinding.ActivityMainBinding;
import com.farhan.boltechai.service.SpeechService;
import com.farhan.boltechai.ui.about.AboutActivity;
import com.farhan.boltechai.ui.adapter.CategoryAdapter;
import com.farhan.boltechai.ui.board.BoardActivity;
import com.farhan.boltechai.ui.history.UsageHistoryActivity;
import com.farhan.boltechai.ui.hospital.HospitalBoardActivity;
import com.farhan.boltechai.ui.pictogram.PictogramSearchActivity;
import com.farhan.boltechai.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity — Main dashboard of Bolte Chai displaying communication categories.
 *
 * Lab Topic: Activity lifecycle, Explicit Intent, Bound Service, RecyclerView
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private SpeechService speechService;
    private boolean isBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SpeechService.LocalBinder binder = (SpeechService.LocalBinder) service;
            speechService = binder.getService();
            isBound = true;
            Log.d(TAG, "onServiceConnected: Bound to SpeechService");
            if (speechService != null && !speechService.isBanglaVoiceAvailable()) {
                Toast.makeText(MainActivity.this, R.string.bangla_voice_missing, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            Log.d(TAG, "onServiceDisconnected: Unbound from SpeechService");
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lab Topic: Activity lifecycle - onCreate
        Log.d(TAG, "onCreate: MainActivity created");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        setupCategoriesGrid();

        binding.sentenceStripView.setOnSpeakClickListener(sentence -> {
            if (isBound && speechService != null) {
                speechService.speak(sentence);
            } else {
                Toast.makeText(this, R.string.service_not_bound, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoriesGrid() {
        int columns = BolteChaiApp.getInstance().getGridSize();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        List<CategoryAdapter.CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_FOOD, getString(R.string.cat_food), R.drawable.ic_cat_food));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_NEEDS, getString(R.string.cat_needs), R.drawable.ic_cat_needs));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_FEELINGS, getString(R.string.cat_feelings), R.drawable.ic_cat_feelings));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_FAMILY, getString(R.string.cat_family), R.drawable.ic_cat_family));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_SCHOOL, getString(R.string.cat_school), R.drawable.ic_cat_school));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_MARKET, getString(R.string.cat_market), R.drawable.ic_cat_market));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_MOSQUE, getString(R.string.cat_mosque), R.drawable.ic_cat_mosque));
        categories.add(new CategoryAdapter.CategoryItem(AppConstants.CAT_HOSPITAL, getString(R.string.cat_hospital), R.drawable.ic_cat_hospital));

        CategoryAdapter adapter = new CategoryAdapter(this, categories, item -> {
            // Lab Topic: Explicit Intent
            Intent intent;
            if (AppConstants.CAT_HOSPITAL.equals(item.key)) {
                intent = new Intent(MainActivity.this, HospitalBoardActivity.class);
            } else {
                intent = new Intent(MainActivity.this, BoardActivity.class);
                intent.putExtra(AppConstants.EXTRA_CATEGORY_KEY, item.key);
                intent.putExtra(AppConstants.EXTRA_CATEGORY_NAME, item.displayName);
            }
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_language) {
            BolteChaiApp.getInstance().toggleLanguage();
            recreate();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, UsageHistoryActivity.class));
            return true;
        } else if (id == R.id.action_pictogram_search) {
            startActivity(new Intent(this, PictogramSearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Lab Topic: Activity lifecycle - onStart
        Log.d(TAG, "onStart: MainActivity started");

        // Lab Topic: Service - bind to SpeechService
        Intent intent = new Intent(this, SpeechService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lab Topic: Activity lifecycle - onResume
        Log.d(TAG, "onResume: MainActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lab Topic: Activity lifecycle - onPause
        Log.d(TAG, "onPause: MainActivity paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Lab Topic: Activity lifecycle - onStop
        Log.d(TAG, "onStop: MainActivity stopped");

        // Lab Topic: Service - unbind from SpeechService
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Lab Topic: Activity lifecycle - onDestroy
        Log.d(TAG, "onDestroy: MainActivity destroyed");
    }
}
