package com.farhan.boltechai.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.firebase.FirebaseSyncManager;
import com.farhan.boltechai.databinding.ActivityAuthBinding;
import com.farhan.boltechai.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseUser;

/**
 * AuthActivity — Provides Email/Password Sign In & Sign Up,
 * plus Guest Mode (Anonymous login) to access Bolte Chai.
 *
 * Lab Topic: Firebase (Auth + Firestore), Explicit Intent
 */
public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private FirebaseSyncManager syncManager;
    private boolean isRegisterMode = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        syncManager = FirebaseSyncManager.getInstance();

        setupUI();
    }

    private void setupUI() {
        updateModeUI();

        // Toggle between Sign In and Sign Up
        binding.btnToggleMode.setOnClickListener(v -> {
            isRegisterMode = !isRegisterMode;
            updateModeUI();
        });

        // Primary action (Sign In / Sign Up)
        binding.btnPrimary.setOnClickListener(v -> handleAuthAction());

        // Guest Mode
        binding.btnGuest.setOnClickListener(v -> handleGuestAction());
    }

    private void updateModeUI() {
        if (isRegisterMode) {
            binding.tvTitle.setText(R.string.auth_register_btn);
            binding.btnPrimary.setText(R.string.auth_register_btn);
            binding.btnToggleMode.setText(R.string.auth_toggle_login);
        } else {
            binding.tvTitle.setText(R.string.auth_welcome);
            binding.btnPrimary.setText(R.string.auth_login_btn);
            binding.btnToggleMode.setText(R.string.auth_toggle_register);
        }
    }

    private void handleAuthAction() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.auth_error_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, R.string.auth_error_password_length, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        if (isRegisterMode) {
            syncManager.createAccount(email, password, new FirebaseSyncManager.AuthListener() {
                @Override
                public void onAuthSuccess(FirebaseUser user) {
                    setLoading(false);
                    Toast.makeText(AuthActivity.this, R.string.auth_success_register, Toast.LENGTH_SHORT).show();
                    // Sync initial boards to Firestore for new user
                    syncManager.syncBoards(AuthActivity.this);
                    navigateToMain();
                }

                @Override
                public void onAuthError(String error) {
                    setLoading(false);
                    Toast.makeText(AuthActivity.this, error != null ? error : "Authentication failed", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            syncManager.signInWithEmail(email, password, new FirebaseSyncManager.AuthListener() {
                @Override
                public void onAuthSuccess(FirebaseUser user) {
                    setLoading(false);
                    Toast.makeText(AuthActivity.this, R.string.auth_success_login, Toast.LENGTH_SHORT).show();
                    // Restore boards/data from Firestore for returning user
                    syncManager.downloadBoards(null);
                    navigateToMain();
                }

                @Override
                public void onAuthError(String error) {
                    setLoading(false);
                    Toast.makeText(AuthActivity.this, error != null ? error : "Login failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void handleGuestAction() {
        setLoading(true);
        syncManager.signInAnonymously(new FirebaseSyncManager.AuthListener() {
            @Override
            public void onAuthSuccess(FirebaseUser user) {
                setLoading(false);
                navigateToMain();
            }

            @Override
            public void onAuthError(String error) {
                setLoading(false);
                // Allow guest offline fallback even if network fails
                navigateToMain();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnPrimary.setEnabled(!loading);
        binding.btnGuest.setEnabled(!loading);
        binding.btnToggleMode.setEnabled(!loading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
