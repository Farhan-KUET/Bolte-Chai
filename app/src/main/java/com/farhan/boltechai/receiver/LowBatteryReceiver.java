package com.farhan.boltechai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.farhan.boltechai.R;

/**
 * LowBatteryReceiver — Warns the caregiver when battery is low.
 *
 * Lab Topic: BroadcastReceiver
 *
 * Listens for ACTION_BATTERY_LOW system broadcast.
 * When triggered, shows a Toast notification to alert the caregiver
 * that the device battery is low and the AAC app may stop working.
 */
public class LowBatteryReceiver extends BroadcastReceiver {

    private static final String TAG = "LowBatteryReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lab Topic: BroadcastReceiver — receiving system broadcast
        if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
            Log.w(TAG, "onReceive: Battery is LOW — alerting caregiver");

            // Show a prominent warning toast
            String message = context.getString(R.string.low_battery_warning);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            // Could also show a notification here for more visibility
            // (keeping it simple with Toast for lab demonstration)
        }
    }
}
