package com.farhan.boltechai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.farhan.boltechai.data.firebase.FirebaseSyncManager;

/**
 * NetworkReceiver — Triggers Firebase sync when network connectivity returns.
 *
 * Lab Topic: BroadcastReceiver
 *
 * Listens for CONNECTIVITY_CHANGE broadcasts. When the device regains
 * network connectivity, triggers a sync of board data with Firestore.
 * This ensures offline changes are uploaded when the network is back.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lab Topic: BroadcastReceiver — receiving connectivity change
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    Log.d(TAG, "onReceive: Network is back — triggering sync");
                    // Trigger Firebase sync when connectivity returns
                    FirebaseSyncManager.getInstance().syncBoards(context);
                } else {
                    Log.d(TAG, "onReceive: Network lost");
                }
            }
        }
    }
}
