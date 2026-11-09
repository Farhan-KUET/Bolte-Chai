package com.farhan.boltechai.ui.pictogram;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.db.entity.TileEntity;
import com.farhan.boltechai.data.model.PictogramResult;
import com.farhan.boltechai.databinding.ActivityPictogramSearchBinding;
import com.farhan.boltechai.ui.adapter.PictogramAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity to search ARASAAC API for pictograms.
 * 
 * Lab Topic: Networking with JSON parsing, RecyclerView
 */
public class PictogramSearchActivity extends AppCompatActivity {

    private ActivityPictogramSearchBinding binding;
    private PictogramAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPictogramSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.search_pictogram);
        }

        setupRecyclerView();
        setupSearch();
    }

    private void setupRecyclerView() {
        binding.recyclerSearchResults.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PictogramAdapter(this, pictogram -> savePictogramAsTile(pictogram));
        binding.recyclerSearchResults.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = binding.editSearch.getText() != null ? binding.editSearch.getText().toString().trim() : "";
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        binding.progressLoading.setVisibility(View.VISIBLE);

        String url = AppConstants.PICTOGRAM_SEARCH_URL + query;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    binding.progressLoading.setVisibility(View.GONE);
                    Toast.makeText(PictogramSearchActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    Type listType = new TypeToken<ArrayList<PictogramResult>>() {}.getType();
                    List<PictogramResult> results = gson.fromJson(json, listType);

                    runOnUiThread(() -> {
                        binding.progressLoading.setVisibility(View.GONE);
                        if (results != null) {
                            adapter.submitList(results);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        binding.progressLoading.setVisibility(View.GONE);
                        Toast.makeText(PictogramSearchActivity.this, "No pictograms found", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void savePictogramAsTile(PictogramResult pictogram) {
        String lang = BolteChaiApp.getInstance().getCurrentLanguage();
        String label = pictogram.getLabel(lang);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int count = db.tileDao().getTileCountForCategory(AppConstants.CAT_NEEDS);
            TileEntity tile = new TileEntity(
                    AppConstants.CAT_NEEDS,
                    label,
                    label,
                    pictogram.getImageUrl(),
                    true,
                    count
            );
            db.tileDao().insertTile(tile);

            runOnUiThread(() -> {
                Toast.makeText(this, "Tile added: " + label, Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
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
