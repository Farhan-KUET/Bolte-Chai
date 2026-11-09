package com.farhan.boltechai.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * UsageEntity — Room entity tracking how often each tile is used.
 *
 * Lab Topic: Room database
 *
 * Records tile usage frequency for the "most-used words" feature.
 */
@Entity(tableName = "usage_history")
public class UsageEntity {

    @PrimaryKey
    @ColumnInfo(name = "tile_id")
    private long tileId;

    /** Number of times this tile has been tapped/used */
    @ColumnInfo(name = "use_count")
    private int useCount;

    /** Timestamp of last usage */
    @ColumnInfo(name = "last_used")
    private long lastUsed;

    /** Cached label for display without join */
    @NonNull
    @ColumnInfo(name = "label_bn")
    private String labelBn;

    /** Cached English label */
    @ColumnInfo(name = "label_en")
    private String labelEn;

    /** Cached category */
    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    // ===== Constructor =====
    public UsageEntity(long tileId, int useCount, long lastUsed,
                       @NonNull String labelBn, String labelEn,
                       @NonNull String category) {
        this.tileId = tileId;
        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.labelBn = labelBn;
        this.labelEn = labelEn;
        this.category = category;
    }

    // ===== Getters & Setters =====
    public long getTileId() { return tileId; }
    public void setTileId(long tileId) { this.tileId = tileId; }

    public int getUseCount() { return useCount; }
    public void setUseCount(int useCount) { this.useCount = useCount; }

    public long getLastUsed() { return lastUsed; }
    public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }

    @NonNull
    public String getLabelBn() { return labelBn; }
    public void setLabelBn(@NonNull String labelBn) { this.labelBn = labelBn; }

    public String getLabelEn() { return labelEn; }
    public void setLabelEn(String labelEn) { this.labelEn = labelEn; }

    @NonNull
    public String getCategory() { return category; }
    public void setCategory(@NonNull String category) { this.category = category; }
}
