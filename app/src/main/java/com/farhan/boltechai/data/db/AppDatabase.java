package com.farhan.boltechai.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.farhan.boltechai.data.db.dao.TileDao;
import com.farhan.boltechai.data.db.dao.UsageDao;
import com.farhan.boltechai.data.db.entity.TileEntity;
import com.farhan.boltechai.data.db.entity.UsageEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppDatabase — Room database for BolteChai.
 *
 * Lab Topic: Room database
 *
 * Contains two tables:
 * - tiles: stores all picture tiles (built-in + user-created)
 * - usage_history: tracks how often each tile is used
 *
 * On first creation, seeds the database with default Bangla tiles
 * via DatabaseSeeder.
 */
@Database(entities = {TileEntity.class, UsageEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    /** Thread pool for database operations off the main thread */
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public abstract TileDao tileDao();
    public abstract UsageDao usageDao();

    /**
     * Returns the singleton database instance.
     * On first creation, seeds default tiles.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "boltechai_db"
                    )
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Seed default tiles on first creation
                            databaseWriteExecutor.execute(() -> {
                                AppDatabase database = getInstance(context);
                                DatabaseSeeder.seedDefaultTiles(database.tileDao());
                            });
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
