package com.farhan.boltechai.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.farhan.boltechai.data.db.entity.TileEntity;

import java.util.List;

/**
 * TileDao — Data Access Object for TileEntity.
 *
 * Lab Topic: Room database
 *
 * Provides CRUD operations for picture tiles stored in the Room database.
 */
@Dao
public interface TileDao {

    /** Get all tiles for a specific category/board */
    @Query("SELECT * FROM tiles WHERE category = :category ORDER BY sort_order ASC")
    List<TileEntity> getTilesByCategory(String category);

    /** Get all tiles across all categories */
    @Query("SELECT * FROM tiles ORDER BY category, sort_order ASC")
    List<TileEntity> getAllTiles();

    /** Get a single tile by ID */
    @Query("SELECT * FROM tiles WHERE id = :id")
    TileEntity getTileById(long id);

    /** Get only custom (user-created) tiles */
    @Query("SELECT * FROM tiles WHERE is_custom = 1 ORDER BY category, sort_order ASC")
    List<TileEntity> getCustomTiles();

    /** Get all unique category names */
    @Query("SELECT DISTINCT category FROM tiles ORDER BY category")
    List<String> getAllCategories();

    /** Get tile count for a category */
    @Query("SELECT COUNT(*) FROM tiles WHERE category = :category")
    int getTileCountForCategory(String category);

    /** Insert a single tile */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTile(TileEntity tile);

    /** Insert multiple tiles at once */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TileEntity> tiles);

    /** Update a tile */
    @Update
    void updateTile(TileEntity tile);

    /** Delete a tile */
    @Delete
    void deleteTile(TileEntity tile);

    /** Delete all custom tiles */
    @Query("DELETE FROM tiles WHERE is_custom = 1")
    void deleteAllCustomTiles();

    /** Delete all tiles (for data wipe) */
    @Query("DELETE FROM tiles")
    void deleteAllTiles();

    /** Count tiles in a category */
    @Query("SELECT COUNT(*) FROM tiles WHERE category = :category")
    int countTilesInCategory(String category);

    /** Search tiles by label (Bangla or English) */
    @Query("SELECT * FROM tiles WHERE label_bn LIKE '%' || :query || '%' " +
           "OR label_en LIKE '%' || :query || '%'")
    List<TileEntity> searchTiles(String query);
}
