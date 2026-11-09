package com.farhan.boltechai.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.data.firebase.FirebaseSyncManager;
import com.farhan.boltechai.databinding.ActivitySplashBinding;
import com.farhan.boltechai.ui.auth.AuthActivity;
import com.farhan.boltechai.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DELAY_MS = 2000;
    
    private ActivitySplashBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lab Topic: Activity lifecycle - onCreate
        Log.d(TAG, "onCreate: SplashActivity created");

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Delay and then check auth / start appropriate activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Lab Topic: Explicit Intent
            FirebaseUser user = FirebaseSyncManager.getInstance().getCurrentUser();
            Intent intent;
            if (user != null) {
                // User is already signed in
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // First time or signed out: go to Login/Register screen
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            // Finish so user cannot return to splash
            finish();
        }, SPLASH_DELAY_MS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Lab Topic: Activity lifecycle - onStart
        Log.d(TAG, "onStart: SplashActivity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lab Topic: Activity lifecycle - onResume
        Log.d(TAG, "onResume: SplashActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lab Topic: Activity lifecycle - onPause
        Log.d(TAG, "onPause: SplashActivity paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Lab Topic: Activity lifecycle - onStop
        Log.d(TAG, "onStop: SplashActivity stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Lab Topic: Activity lifecycle - onDestroy
        Log.d(TAG, "onDestroy: SplashActivity destroyed");
    }
}
