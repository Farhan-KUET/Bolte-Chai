package com.farhan.boltechai.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * TileEntity — Room entity representing a single picture tile.
 *
 * Lab Topic: Room database
 *
 * Each tile belongs to a category (board), has a Bangla label,
 * an optional English label, and an image path (either a resource name
 * for built-in tiles or a file path for user-created tiles).
 */
@Entity(tableName = "tiles")
public class TileEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    /** Category key (e.g., "food", "needs") */
    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    /** Bangla label displayed on the tile */
    @NonNull
    @ColumnInfo(name = "label_bn")
    private String labelBn;

    /** English label (optional, for English mode) */
    @ColumnInfo(name = "label_en")
    private String labelEn;

    /**
     * Image source:
     * - For built-in tiles: drawable resource name (e.g., "ic_rice")
     * - For user-created tiles: absolute file path to the image
     */
    @ColumnInfo(name = "image_path")
    private String imagePath;

    /** Whether this is a user-created tile (vs. built-in) */
    @ColumnInfo(name = "is_custom")
    private boolean isCustom;

    /** Display order within the category */
    @ColumnInfo(name = "sort_order")
    private int sortOrder;

    /** Firestore document ID for sync (null if not synced) */
    @ColumnInfo(name = "firestore_id")
    private String firestoreId;

    // ===== Constructor =====
    public TileEntity(@NonNull String category, @NonNull String labelBn,
                      String labelEn, String imagePath,
                      boolean isCustom, int sortOrder) {
        this.category = category;
        this.labelBn = labelBn;
        this.labelEn = labelEn;
        this.imagePath = imagePath;
        this.isCustom = isCustom;
        this.sortOrder = sortOrder;
    }

    // ===== Getters & Setters =====
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull
    public String getCategory() { return category; }
    public void setCategory(@NonNull String category) { this.category = category; }

    @NonNull
    public String getLabelBn() { return labelBn; }
    public void setLabelBn(@NonNull String labelBn) { this.labelBn = labelBn; }

    public String getLabelEn() { return labelEn; }
    public void setLabelEn(String labelEn) { this.labelEn = labelEn; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getFirestoreId() { return firestoreId; }
    public void setFirestoreId(String firestoreId) { this.firestoreId = firestoreId; }

    /**
     * Returns the label to display based on the current language.
     */
    public String getDisplayLabel(String langCode) {
        if ("en".equals(langCode) && labelEn != null && !labelEn.isEmpty()) {
            return labelEn;
        }
        return labelBn;
    }
}
