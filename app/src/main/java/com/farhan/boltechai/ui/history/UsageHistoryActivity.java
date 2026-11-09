package com.farhan.boltechai.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.db.entity.UsageEntity;
import com.farhan.boltechai.databinding.ActivityUsageHistoryBinding;
import com.farhan.boltechai.ui.adapter.UsageAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Usage history activity showing most-used words.
 * 
 * Lab Topic: Room database, RecyclerView with custom adapter
 */
public class UsageHistoryActivity extends AppCompatActivity {

    private ActivityUsageHistoryBinding binding;
    private UsageAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsageHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.history);
        }

        setupRecyclerView();
        loadUsageData();
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsageAdapter();
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadUsageData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<UsageEntity> usageData = db.usageDao().getAllUsageSorted();
            
            runOnUiThread(() -> {
                adapter.submitList(usageData);
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
