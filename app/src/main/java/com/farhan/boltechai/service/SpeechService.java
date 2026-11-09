package com.farhan.boltechai.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

/**
 * SpeechService — Bound service that queues and speaks text in Bangla.
 *
 * Lab Topic: Service (a bound SpeechService that queues speech)
 *
 * Features:
 * - Initializes Android TextToSpeech engine with Bangla locale
 * - Queues speech requests so multiple phrases play in order
 * - Reports whether Bangla voice is available
 * - Bound service pattern: activities bind/unbind in onStart/onStop
 */
public class SpeechService extends Service implements TextToSpeech.OnInitListener {

    private static final String TAG = "SpeechService";

    /** Binder for bound service communication */
    private final IBinder binder = new LocalBinder();

    /** Android TextToSpeech engine */
    private TextToSpeech tts;

    /** Whether TTS is initialized and ready */
    private boolean isReady = false;

    /** Whether Bangla voice is available */
    private boolean isBanglaAvailable = false;

    /** Queue of texts waiting to be spoken */
    private final Queue<String> speechQueue = new LinkedList<>();

    /** Whether TTS is currently speaking */
    private boolean isSpeaking = false;

    /** Counter for unique utterance IDs */
    private int utteranceCounter = 0;

    /** Listener for TTS status updates */
    private SpeechStatusListener statusListener;

    /**
     * Callback interface for speech status events.
     */
    public interface SpeechStatusListener {
        void onBanglaUnavailable();
        void onSpeechStarted();
        void onSpeechDone();
        void onSpeechError(String error);
    }

    /**
     * Binder class allowing activities to get a reference to this service.
     * Lab Topic: Service — Binder pattern
     */
    public class SpeechBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }

    public class LocalBinder extends SpeechBinder {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Initializing SpeechService");
        // Lab Topic: Service — initialize TTS in onCreate
        tts = new TextToSpeech(this, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Client bound to SpeechService");
        // Lab Topic: Service — return binder for bound service
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: Client unbound from SpeechService");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Shutting down SpeechService");
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    /**
     * TextToSpeech.OnInitListener callback.
     * Checks if Bangla voice is available and configures TTS.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Try to set Bangla locale
            Locale bangla = new Locale("bn");
            int result = tts.setLanguage(bangla);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Bangla voice not available
                isBanglaAvailable = false;
                Log.w(TAG, "onInit: Bangla voice NOT available");

                // Notify listener so UI can show warning
                if (statusListener != null) {
                    statusListener.onBanglaUnavailable();
                }
            } else {
                isBanglaAvailable = true;
                Log.d(TAG, "onInit: Bangla voice available and set");
            }

            // Set up utterance progress listener for queue management
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.d(TAG, "Speaking: " + utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.d(TAG, "Done speaking: " + utteranceId);
                    isSpeaking = false;
                    // Speak next in queue if any
                    processQueue();
                    if (speechQueue.isEmpty() && statusListener != null) {
                        statusListener.onSpeechDone();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e(TAG, "Error speaking: " + utteranceId);
                    isSpeaking = false;
                    if (statusListener != null) {
                        statusListener.onSpeechError("TTS error for: " + utteranceId);
                    }
                    processQueue();
                }
            });

            isReady = true;
        } else {
            Log.e(TAG, "onInit: TTS initialization failed with status " + status);
            isReady = false;
        }
    }

    /**
     * Queues text to be spoken in Bangla.
     * If TTS is not ready or Bangla is unavailable, notifies the listener.
     */
    public void speak(String text) {
        if (!isReady) {
            Log.w(TAG, "speak: TTS not ready");
            return;
        }
        if (!isBanglaAvailable) {
            if (statusListener != null) {
                statusListener.onBanglaUnavailable();
            }
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        speechQueue.add(text);
        if (!isSpeaking) {
            processQueue();
        }
        if (statusListener != null) {
            statusListener.onSpeechStarted();
        }
    }

    /**
     * Speaks the next text in the queue.
     */
    private void processQueue() {
        if (!speechQueue.isEmpty() && !isSpeaking) {
            String text = speechQueue.poll();
            isSpeaking = true;
            String utteranceId = "utt_" + (++utteranceCounter);

            tts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
            Log.d(TAG, "processQueue: Speaking \"" + text + "\" with id " + utteranceId);
        }
    }

    /**
     * Stops all speech and clears the queue.
     */
    public void stopSpeaking() {
        speechQueue.clear();
        isSpeaking = false;
        if (tts != null) {
            tts.stop();
        }
    }

    /**
     * Returns whether Bangla TTS voice is available.
     */
    public boolean isBanglaAvailable() {
        return isBanglaAvailable;
    }

    public boolean isBanglaVoiceAvailable() {
        return isBanglaAvailable;
    }

    /**
     * Returns whether TTS is initialized and ready.
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Sets the speech status listener.
     */
    public void setStatusListener(SpeechStatusListener listener) {
        this.statusListener = listener;
    }
}
