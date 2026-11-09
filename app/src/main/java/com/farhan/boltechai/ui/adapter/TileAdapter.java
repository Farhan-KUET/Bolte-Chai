package com.farhan.boltechai.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.entity.TileEntity;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.List;

/**
 * TileAdapter — RecyclerView adapter for picture tiles within a board.
 *
 * Lab Topic: RecyclerView with custom item layouts, adapters and ViewHolders
 *
 * Displays picture tiles with Bangla labels in a grid. Supports:
 * - Built-in drawable resources and custom photo file paths
 * - Scanning mode highlight (one tile highlighted at a time)
 * - Click to add tile to sentence strip
 */
public class TileAdapter extends RecyclerView.Adapter<TileAdapter.TileViewHolder> {

    /** Listener for tile clicks */
    public interface OnTileClickListener {
        void onTileClick(TileEntity tile, int position);
    }

    private final Context context;
    private final List<TileEntity> tiles;
    private final OnTileClickListener listener;
    private final String currentLang;

    /** Index of the currently highlighted tile (scanning mode), -1 if none */
    private int highlightedPosition = -1;

    public TileAdapter(Context context, List<TileEntity> tiles,
                       String currentLang, OnTileClickListener listener) {
        this.context = context;
        this.tiles = tiles;
        this.currentLang = currentLang;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Lab Topic: RecyclerView — inflating custom item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tile, parent, false);
        return new TileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TileViewHolder holder, int position) {
        TileEntity tile = tiles.get(position);
        holder.bind(tile, position);
    }

    @Override
    public int getItemCount() {
        return tiles.size();
    }

    /**
     * Updates the highlighted tile for scanning mode.
     * @param position the tile to highlight, or -1 to clear
     */
    public void setHighlightedPosition(int position) {
        int oldPos = highlightedPosition;
        highlightedPosition = position;
        if (oldPos >= 0 && oldPos < getItemCount()) {
            notifyItemChanged(oldPos);
        }
        if (position >= 0 && position < getItemCount()) {
            notifyItemChanged(position);
        }
    }

    /**
     * Returns the currently highlighted position.
     */
    public int getHighlightedPosition() {
        return highlightedPosition;
    }

    /**
     * Returns the tile at the given position.
     */
    public TileEntity getTileAt(int position) {
        if (position >= 0 && position < tiles.size()) {
            return tiles.get(position);
        }
        return null;
    }

    /**
     * ViewHolder for a picture tile.
     *
     * Lab Topic: RecyclerView — ViewHolder pattern
     */
    class TileViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView tileImage;
        private final TextView tileLabel;
        private final View highlightOverlay;

        TileViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.tile_card);
            tileImage = itemView.findViewById(R.id.tile_image);
            tileLabel = itemView.findViewById(R.id.tile_label);
            highlightOverlay = itemView.findViewById(R.id.tile_highlight_overlay);
        }

        void bind(TileEntity tile, int position) {
            // Set label based on current language
            tileLabel.setText(tile.getDisplayLabel(currentLang));

            // Load image: check if custom file path or drawable resource name
            if (tile.isCustom() && tile.getImagePath() != null) {
                // Custom tile: load from file path using Glide
                File imageFile = new File(tile.getImagePath());
                Glide.with(context)
                        .load(imageFile)
                        .placeholder(R.drawable.ic_placeholder)
                        .centerCrop()
                        .into(tileImage);
            } else {
                // Built-in tile: load from drawable resources
                int resId = context.getResources().getIdentifier(
                        tile.getImagePath(), "drawable", context.getPackageName());
                if (resId != 0) {
                    tileImage.setImageResource(resId);
                } else {
                    // Fallback placeholder if drawable not found
                    tileImage.setImageResource(R.drawable.ic_placeholder);
                }
            }

            // Scanning mode highlight (Lab Topic: Scanning mode)
            if (position == highlightedPosition) {
                highlightOverlay.setVisibility(View.VISIBLE);
                cardView.setStrokeColor(ContextCompat.getColor(
                        context, R.color.scanning_highlight));
                cardView.setStrokeWidth(6);
            } else {
                highlightOverlay.setVisibility(View.GONE);
                cardView.setStrokeColor(ContextCompat.getColor(
                        context, R.color.tile_border));
                cardView.setStrokeWidth(2);
            }

            // TalkBack accessibility label
            cardView.setContentDescription(tile.getDisplayLabel(currentLang));

            // Click listener — adds tile to sentence strip
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTileClick(tile, position);
                }
            });
        }
    }
}
