package com.farhan.boltechai.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.db.dao.TileDao;

import java.util.List;

/**
 * BoardContentProvider — Read-only ContentProvider exposing board (category) names.
 *
 * Lab Topic: ContentProvider — custom read-only provider
 *
 * Other apps can query this provider to get the list of available
 * communication boards. This demonstrates the ContentProvider pattern
 * without exposing sensitive user data.
 *
 * Authority: com.farhan.boltechai.boards
 * URI: content://com.farhan.boltechai.boards/categories
 *
 * Returns columns: _id, category_key
 */
public class BoardContentProvider extends ContentProvider {

    private static final int CATEGORIES = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Lab Topic: ContentProvider — URI matching
        uriMatcher.addURI(AppConstants.AUTHORITY_BOARDS, "categories", CATEGORIES);
    }

    private TileDao tileDao;

    @Override
    public boolean onCreate() {
        // Lab Topic: ContentProvider — initialization
        // Lazy-init: tileDao is obtained when first queried
        return true;
    }

    /**
     * Returns the TileDao, initializing it lazily.
     */
    private TileDao getTileDao() {
        if (tileDao == null && getContext() != null) {
            tileDao = AppDatabase.getInstance(getContext()).tileDao();
        }
        return tileDao;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // Lab Topic: ContentProvider — query implementation
        if (uriMatcher.match(uri) == CATEGORIES) {
            TileDao dao = getTileDao();
            if (dao == null) return null;

            // Get distinct category names from the database
            List<String> categories = dao.getAllCategories();

            // Build a MatrixCursor with the results
            MatrixCursor cursor = new MatrixCursor(
                    new String[]{"_id", "category_key"});

            int id = 0;
            for (String category : categories) {
                cursor.addRow(new Object[]{id++, category});
            }

            // Notify observers of changes
            if (getContext() != null) {
                cursor.setNotificationUri(
                        getContext().getContentResolver(), uri);
            }

            return cursor;
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Lab Topic: ContentProvider — MIME type
        if (uriMatcher.match(uri) == CATEGORIES) {
            return "vnd.android.cursor.dir/vnd.com.farhan.boltechai.category";
        }
        return null;
    }

    // ===== Read-only: insert, update, delete are not supported =====

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException(
                "BoardContentProvider is read-only");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException(
                "BoardContentProvider is read-only");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException(
                "BoardContentProvider is read-only");
    }
}
