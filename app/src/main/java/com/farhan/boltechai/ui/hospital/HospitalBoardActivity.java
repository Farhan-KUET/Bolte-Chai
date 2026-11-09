package com.farhan.boltechai.ui.hospital;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.farhan.boltechai.AppConstants;
import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.databinding.ActivityHospitalBoardBinding;
import com.farhan.boltechai.service.SpeechService;

/**
 * Hospital body board activity.
 * Allows users to indicate which body part hurts.
 * 
 * Lab Topic: Custom view (SentenceStripView), Explicit Intent, Service Binding
 */
public class HospitalBoardActivity extends AppCompatActivity {

    private ActivityHospitalBoardBinding binding;
    private SpeechService speechService;
    private boolean isBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SpeechService.LocalBinder binder = (SpeechService.LocalBinder) service;
            speechService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(BolteChaiApp.getInstance().getLocaleContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.hospital_board_title);
        }

        setupClickListeners();
        
        // Bind to SpeechService
        Intent intent = new Intent(this, SpeechService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupClickListeners() {
        setupBodyPart(binding.btnHead, getString(R.string.hospital_head));
        setupBodyPart(binding.btnEyes, getString(R.string.hospital_eye));
        setupBodyPart(binding.btnEars, getString(R.string.hospital_ear));
        setupBodyPart(binding.btnNose, getString(R.string.hospital_nose));
        setupBodyPart(binding.btnTeeth, getString(R.string.hospital_teeth));
        setupBodyPart(binding.btnThroat, getString(R.string.hospital_throat));
        setupBodyPart(binding.btnChest, getString(R.string.hospital_chest));
        setupBodyPart(binding.btnStomach, getString(R.string.hospital_stomach));
        setupBodyPart(binding.btnBack, getString(R.string.hospital_back));
        setupBodyPart(binding.btnHands, getString(R.string.hospital_hand));
        setupBodyPart(binding.btnFeet, getString(R.string.hospital_foot));
        setupBodyPart(binding.btnKnees, getString(R.string.hospital_knee));
        
        binding.sentenceStrip.setOnSpeakClickListener(sentence -> {
            if (isBound && speechService != null) {
                speechService.speak(sentence);
            }
        });
    }

    private void setupBodyPart(View button, String bodyPartBn) {
        if (button != null) {
            button.setOnClickListener(v -> {
                binding.sentenceStrip.addHospitalPhrase(bodyPartBn);
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
