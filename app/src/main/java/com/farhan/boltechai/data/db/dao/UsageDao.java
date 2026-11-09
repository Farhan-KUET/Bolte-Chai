package com.farhan.boltechai.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.farhan.boltechai.data.db.entity.UsageEntity;

import java.util.List;

/**
 * UsageDao — Data Access Object for UsageEntity.
 *
 * Lab Topic: Room database
 *
 * Tracks and queries tile usage history for the most-used-words feature.
 */
@Dao
public interface UsageDao {

    /** Get all usage records ordered by count (most used first) */
    @Query("SELECT * FROM usage_history ORDER BY use_count DESC")
    List<UsageEntity> getAllUsageSorted();

    /** Get top N most-used tiles */
    @Query("SELECT * FROM usage_history ORDER BY use_count DESC LIMIT :limit")
    List<UsageEntity> getTopUsed(int limit);

    /** Get usage record for a specific tile */
    @Query("SELECT * FROM usage_history WHERE tile_id = :tileId")
    UsageEntity getUsageForTile(long tileId);

    /** Insert or update usage (REPLACE strategy increments in app logic) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UsageEntity usage);

    /** Get total number of recorded usages */
    @Query("SELECT SUM(use_count) FROM usage_history")
    int getTotalUsageCount();

    /** Delete all usage history (for data wipe) */
    @Query("DELETE FROM usage_history")
    void deleteAllUsage();
}
