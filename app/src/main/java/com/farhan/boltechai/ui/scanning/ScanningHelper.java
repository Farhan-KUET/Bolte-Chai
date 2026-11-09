package com.farhan.boltechai.ui.scanning;

import android.os.Handler;
import android.os.Looper;

import com.farhan.boltechai.ui.adapter.TileAdapter;

/**
 * ScanningHelper — Manages tile-by-tile scanning/highlighting at adjustable speed.
 *
 * Lab Topic: Scanning mode
 *
 * Features:
 * - Highlights tiles one by one in sequence
 * - Adjustable scanning speed (delay between highlights)
 * - A single tap or volume button selects the highlighted tile
 * - Wraps around to the first tile after the last
 */
public class ScanningHelper {

    /** The adapter being scanned */
    private final TileAdapter adapter;

    /** Handler for posting timed highlights */
    private final Handler handler;

    /** Current highlighted position */
    private int currentPosition = -1;

    /** Scanning speed in milliseconds */
    private int speedMs;

    /** Whether scanning is currently active */
    private boolean isActive = false;

    /** Listener called when scanning changes position */
    private OnScanPositionListener listener;

    public interface OnScanPositionListener {
        void onPositionChanged(int position);
    }

    /**
     * Runnable that advances the highlight to the next tile.
     */
    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isActive || adapter.getItemCount() == 0) return;

            // Move to next position (wrap around)
            currentPosition = (currentPosition + 1) % adapter.getItemCount();
            adapter.setHighlightedPosition(currentPosition);

            if (listener != null) {
                listener.onPositionChanged(currentPosition);
            }

            // Schedule next highlight
            handler.postDelayed(this, speedMs);
        }
    };

    public ScanningHelper(TileAdapter adapter, int speedMs) {
        this.adapter = adapter;
        this.speedMs = speedMs;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Starts the scanning cycle.
     */
    public void start() {
        if (isActive) return;
        isActive = true;
        currentPosition = -1;
        handler.post(scanRunnable);
    }

    /**
     * Stops the scanning cycle and clears the highlight.
     */
    public void stop() {
        isActive = false;
        handler.removeCallbacks(scanRunnable);
        currentPosition = -1;
        adapter.setHighlightedPosition(-1);
    }

    /**
     * Pauses scanning (keeps current position).
     */
    public void pause() {
        isActive = false;
        handler.removeCallbacks(scanRunnable);
    }

    /**
     * Resumes scanning from the current position.
     */
    public void resume() {
        if (isActive) return;
        isActive = true;
        handler.postDelayed(scanRunnable, speedMs);
    }

    /**
     * Returns the current highlighted position.
     * Used when the user taps to select the highlighted tile.
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Updates the scanning speed.
     */
    public void setSpeed(int speedMs) {
        this.speedMs = speedMs;
    }

    /**
     * Sets the scan position change listener.
     */
    public void setOnScanPositionListener(OnScanPositionListener listener) {
        this.listener = listener;
    }

    /**
     * Returns whether scanning is currently active.
     */
    public boolean isActive() {
        return isActive;
    }

    public void startScanning() {
        start();
    }

    public void stopScanning() {
        stop();
    }

    public com.farhan.boltechai.data.db.entity.TileEntity selectCurrent() {
        if (currentPosition >= 0 && currentPosition < adapter.getItemCount()) {
            return adapter.getTileAt(currentPosition);
        }
        return null;
    }
}
