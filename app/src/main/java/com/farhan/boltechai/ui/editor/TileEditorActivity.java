package com.farhan.boltechai.ui.editor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.db.entity.TileEntity;
import com.farhan.boltechai.databinding.ActivityTileEditorBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TileEditorActivity — Allows caregiver or user to add a custom picture tile.
 *
 * Lab Topic: Implicit Intent (Camera, Gallery), ContentProvider (FileProvider), Room database
 */
public class TileEditorActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_PICK = 102;

    private ActivityTileEditorBinding binding;
    private String currentPhotoPath;
    private Uri photoUri;
    private String selectedCategory = AppConstants.CAT_FOOD;

    private final String[] categoryKeys = new String[]{
            AppConstants.CAT_FOOD,
            AppConstants.CAT_NEEDS,
            AppConstants.CAT_FEELINGS,
            AppConstants.CAT_FAMILY,
            AppConstants.CAT_SCHOOL,
            AppConstants.CAT_MARKET,
            AppConstants.CAT_MOSQUE
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTileEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.edit_tile);
        }

        String initialCat = getIntent().getStringExtra(AppConstants.EXTRA_CATEGORY_KEY);
        if (initialCat != null) {
            selectedCategory = initialCat;
        }

        setupCategorySpinner();

        binding.btnCamera.setOnClickListener(v -> dispatchTakePictureIntent());
        binding.btnGallery.setOnClickListener(v -> dispatchPickImageIntent());
        binding.imgPreview.setOnClickListener(v -> dispatchPickImageIntent());

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveTile());
    }

    private void setupCategorySpinner() {
        String[] displayNames = new String[]{
                getString(R.string.cat_food),
                getString(R.string.cat_needs),
                getString(R.string.cat_feelings),
                getString(R.string.cat_family),
                getString(R.string.cat_school),
                getString(R.string.cat_market),
                getString(R.string.cat_mosque)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, displayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        for (int i = 0; i < categoryKeys.length; i++) {
            if (categoryKeys[i].equals(selectedCategory)) {
                binding.spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        // Lab Topic: Implicit Intent for Camera & FileProvider
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this,
                    "com.farhan.boltechai.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchPickImageIntent() {
        // Lab Topic: Implicit Intent for Gallery
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, REQUEST_IMAGE_PICK);
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (currentPhotoPath != null) {
                    Glide.with(this).load(new File(currentPhotoPath)).into(binding.imgPreview);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null && data.getData() != null) {
                Uri selectedUri = data.getData();
                copyGalleryImageLocally(selectedUri);
            }
        }
    }

    private void copyGalleryImageLocally(Uri uri) {
        try {
            File targetFile = createImageFile();
            if (targetFile != null) {
                InputStream in = getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(targetFile);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
                currentPhotoPath = targetFile.getAbsolutePath();
                Glide.with(this).load(targetFile).into(binding.imgPreview);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTile() {
        String label = binding.editLabel.getText() != null ? binding.editLabel.getText().toString().trim() : "";
        if (TextUtils.isEmpty(label)) {
            Toast.makeText(this, R.string.error_label_required, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIdx = binding.spinnerCategory.getSelectedItemPosition();
        if (selectedIdx >= 0 && selectedIdx < categoryKeys.length) {
            selectedCategory = categoryKeys[selectedIdx];
        }

        // Lab Topic: Room Database - insert custom tile
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int count = db.tileDao().getTileCountForCategory(selectedCategory);
            TileEntity tile = new TileEntity(
                    selectedCategory,
                    label,
                    label,
                    currentPhotoPath != null ? currentPhotoPath : "ic_placeholder",
                    currentPhotoPath != null,
                    count
            );
            db.tileDao().insertTile(tile);

            runOnUiThread(() -> {
                setResult(RESULT_OK);
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
