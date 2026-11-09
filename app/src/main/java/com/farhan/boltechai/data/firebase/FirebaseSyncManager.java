package com.farhan.boltechai.data.firebase;

import android.content.Context;
import android.util.Log;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.data.db.AppDatabase;
import com.farhan.boltechai.data.db.entity.TileEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FirebaseSyncManager — Manages Firebase Auth and Firestore sync for boards.
 *
 * Lab Topic: Firebase (Auth + Firestore), Networking
 *
 * Features:
 * - Anonymous or email authentication
 * - Sync board metadata to Firestore (no personal data)
 * - Download board data from Firestore
 * - Delete all user data from Firebase
 *
 * PRIVACY: No child names or ages stored on server.
 * Only board structure (category, labels, tile counts) is synced.
 */
public class FirebaseSyncManager {

    private static final String TAG = "FirebaseSyncManager";
    private static FirebaseSyncManager instance;

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    /** Listener for sync status updates */
    public interface SyncListener {
        void onSyncSuccess();
        void onSyncError(String error);
    }

    /** Listener for auth status updates */
    public interface AuthListener {
        void onAuthSuccess(FirebaseUser user);
        void onAuthError(String error);
    }

    private FirebaseSyncManager() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseSyncManager getInstance() {
        if (instance == null) {
            instance = new FirebaseSyncManager();
        }
        return instance;
    }

    // ===== Authentication =====

    /**
     * Signs in anonymously.
     * Lab Topic: Firebase Auth
     */
    public void signInAnonymously(AuthListener listener) {
        auth.signInAnonymously()
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    Log.d(TAG, "signInAnonymously: Success, uid=" +
                            (user != null ? user.getUid() : "null"));
                    if (listener != null) listener.onAuthSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "signInAnonymously: Failed", e);
                    if (listener != null) listener.onAuthError(e.getMessage());
                });
    }

    /**
     * Signs in with email and password.
     * Lab Topic: Firebase Auth
     */
    public void signInWithEmail(String email, String password, AuthListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    Log.d(TAG, "signInWithEmail: Success");
                    if (listener != null) listener.onAuthSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "signInWithEmail: Failed", e);
                    if (listener != null) listener.onAuthError(e.getMessage());
                });
    }

    /**
     * Creates a new account with email and password.
     */
    public void createAccount(String email, String password, AuthListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    Log.d(TAG, "createAccount: Success");
                    if (listener != null) listener.onAuthSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "createAccount: Failed", e);
                    if (listener != null) listener.onAuthError(e.getMessage());
                });
    }

    /**
     * Signs out the current user.
     */
    public void signOut() {
        auth.signOut();
        Log.d(TAG, "signOut: User signed out");
    }

    /**
     * Returns the current Firebase user, or null if not signed in.
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // ===== Firestore Sync =====

    /**
     * Syncs board metadata to Firestore.
     * Lab Topic: Firebase Firestore
     *
     * PRIVACY: Only syncs board structure (category, labels, counts).
     * No personal data is stored.
     */
    public void syncBoards(Context context) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.w(TAG, "syncBoards: Not signed in, skipping sync");
            return;
        }

        String uid = user.getUid();

        // Run database access off main thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<String> categories = db.tileDao().getAllCategories();

            for (String category : categories) {
                List<TileEntity> tiles = db.tileDao().getTilesByCategory(category);

                // Build board metadata (no personal data)
                Map<String, Object> boardData = new HashMap<>();
                boardData.put("category", category);
                boardData.put("tile_count", tiles.size());
                boardData.put("last_sync", System.currentTimeMillis());

                // Store under user's document
                firestore.collection("users")
                        .document(uid)
                        .collection(AppConstants.FS_COLLECTION_BOARDS)
                        .document(category)
                        .set(boardData)
                        .addOnSuccessListener(aVoid ->
                                Log.d(TAG, "syncBoards: Synced " + category))
                        .addOnFailureListener(e ->
                                Log.e(TAG, "syncBoards: Failed for " + category, e));
            }
        });
    }

    /**
     * Downloads board data from Firestore.
     */
    public void downloadBoards(SyncListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) listener.onSyncError("Not signed in");
            return;
        }

        String uid = user.getUid();

        firestore.collection("users")
                .document(uid)
                .collection(AppConstants.FS_COLLECTION_BOARDS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String category = doc.getString("category");
                        Log.d(TAG, "downloadBoards: Got board " + category);
                    }
                    if (listener != null) listener.onSyncSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "downloadBoards: Failed", e);
                    if (listener != null) listener.onSyncError(e.getMessage());
                });
    }

    /**
     * Deletes all user data from Firebase.
     * PRIVACY: "Delete all my data" feature.
     */
    public void deleteAllUserData(Context context, SyncListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) listener.onSyncSuccess();
            return;
        }

        String uid = user.getUid();

        // Delete Firestore data
        firestore.collection("users")
                .document(uid)
                .collection(AppConstants.FS_COLLECTION_BOARDS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    Log.d(TAG, "deleteAllUserData: Firestore data deleted");

                    // Delete local Room data
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(context);
                        db.tileDao().deleteAllCustomTiles();
                        db.usageDao().deleteAllUsage();
                        Log.d(TAG, "deleteAllUserData: Local data deleted");
                    });

                    if (listener != null) listener.onSyncSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "deleteAllUserData: Failed", e);
                    if (listener != null) listener.onSyncError(e.getMessage());
                });
    }
}
