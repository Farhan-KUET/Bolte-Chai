package com.farhan.boltechai.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.entity.TileEntity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * SentenceStripView — Custom view that shows tapped tiles in order.
 *
 * Lab Topic: Custom view
 *
 * Features:
 * - Displays selected tiles as small chips in a horizontal scroll
 * - Delete button removes the last tile
 * - Clear button removes all tiles
 * - Speak button triggers TTS for the whole sentence
 * - Provides the current sentence text for TTS
 */
public class SentenceStripView extends LinearLayout {

    private HorizontalScrollView scrollView;
    private LinearLayout tileContainer;
    private ImageButton btnDeleteLast;
    private ImageButton btnClearAll;
    private MaterialButton btnSpeak;

    /** The ordered list of tiles currently in the sentence strip */
    private final List<TileEntity> sentenceTiles = new ArrayList<>();

    /** Listener for speak button clicks */
    private OnSpeakClickListener speakListener;

    /** Listener for sentence changes (tile added/removed) */
    private OnSentenceChangedListener sentenceChangedListener;

    public interface OnSpeakClickListener {
        void onSpeakClick(String sentence);
    }

    public interface OnSentenceChangedListener {
        void onSentenceChanged(List<TileEntity> tiles);
    }

    public SentenceStripView(Context context) {
        super(context);
        init(context);
    }

    public SentenceStripView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SentenceStripView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Inflates the sentence strip layout and sets up button listeners.
     */
    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setBackgroundColor(context.getResources().getColor(R.color.surface, context.getTheme()));
        int pad = (int) (8 * context.getResources().getDisplayMetrics().density);
        setPadding(pad, pad, pad, pad);

        // Inflate the sentence strip layout
        LayoutInflater.from(context).inflate(R.layout.view_sentence_strip, this, true);

        scrollView = findViewById(R.id.sentence_scroll);
        tileContainer = findViewById(R.id.sentence_tile_container);
        btnDeleteLast = findViewById(R.id.btn_delete_last);
        btnClearAll = findViewById(R.id.btn_clear_all);
        btnSpeak = findViewById(R.id.btn_speak);

        // Delete last tile
        btnDeleteLast.setOnClickListener(v -> {
            if (!sentenceTiles.isEmpty()) {
                sentenceTiles.remove(sentenceTiles.size() - 1);
                refreshDisplay();
                notifySentenceChanged();
            }
        });

        // Clear all tiles
        btnClearAll.setOnClickListener(v -> {
            sentenceTiles.clear();
            refreshDisplay();
            notifySentenceChanged();
        });

        // Speak the sentence
        btnSpeak.setOnClickListener(v -> {
            if (speakListener != null && !sentenceTiles.isEmpty()) {
                speakListener.onSpeakClick(getSentenceText());
            }
        });

        // Set accessibility descriptions
        btnDeleteLast.setContentDescription(context.getString(R.string.delete));
        btnClearAll.setContentDescription(context.getString(R.string.clear_all));
        btnSpeak.setContentDescription(context.getString(R.string.speak));
    }

    /**
     * Adds a tile to the sentence strip.
     */
    public void addTile(TileEntity tile) {
        sentenceTiles.add(tile);
        refreshDisplay();
        notifySentenceChanged();

        // Scroll to the end to show the newly added tile
        scrollView.post(() -> scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT));
    }

    /**
     * Adds a hospital phrase tile (e.g., "আমার মাথা ব্যথা করছে").
     */
    public void addHospitalPhrase(String bodyPartBn) {
        // Create a temporary tile for the full phrase
        TileEntity phraseTile = new TileEntity(
                "hospital",
                "আমার " + bodyPartBn + " ব্যথা করছে",
                "My " + bodyPartBn + " hurts",
                "ic_pain",
                false,
                0
        );
        sentenceTiles.add(phraseTile);
        refreshDisplay();
        notifySentenceChanged();
        scrollView.post(() -> scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT));
    }

    /**
     * Returns the complete sentence text (Bangla labels joined with spaces).
     */
    public String getSentenceText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentenceTiles.size(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(sentenceTiles.get(i).getLabelBn());
        }
        return sb.toString();
    }

    /**
     * Returns the current list of tiles in the sentence.
     */
    public List<TileEntity> getSentenceTiles() {
        return new ArrayList<>(sentenceTiles);
    }

    /**
     * Clears the sentence strip.
     */
    public void clearAll() {
        sentenceTiles.clear();
        refreshDisplay();
        notifySentenceChanged();
    }

    /**
     * Refreshes the displayed tile chips based on the current list.
     */
    private void refreshDisplay() {
        tileContainer.removeAllViews();
        Context ctx = getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        for (TileEntity tile : sentenceTiles) {
            View chipView = inflater.inflate(
                    R.layout.item_sentence_tile, tileContainer, false);

            TextView label = chipView.findViewById(R.id.sentence_tile_label);
            label.setText(tile.getLabelBn());

            // TalkBack: announce the tile label
            chipView.setContentDescription(tile.getLabelBn());

            tileContainer.addView(chipView);
        }

        // Update button visibility
        boolean hasContent = !sentenceTiles.isEmpty();
        btnDeleteLast.setEnabled(hasContent);
        btnClearAll.setEnabled(hasContent);
        btnSpeak.setEnabled(hasContent);
    }

    private void notifySentenceChanged() {
        if (sentenceChangedListener != null) {
            sentenceChangedListener.onSentenceChanged(getSentenceTiles());
        }
    }

    // ===== Listener Setters =====
    public void setOnSpeakClickListener(OnSpeakClickListener listener) {
        this.speakListener = listener;
    }

    public void setOnSentenceChangedListener(OnSentenceChangedListener listener) {
        this.sentenceChangedListener = listener;
    }
}
