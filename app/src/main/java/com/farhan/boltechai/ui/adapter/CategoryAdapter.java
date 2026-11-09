package com.farhan.boltechai.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farhan.boltechai.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * CategoryAdapter — RecyclerView adapter for the main screen category grid.
 *
 * Lab Topic: RecyclerView with custom item layouts, adapters and ViewHolders
 *
 * Displays category cards (food, needs, feelings, etc.) with an icon
 * and Bangla label. Each card navigates to the corresponding board.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    /**
     * Represents a category with its display name, key, and icon resource.
     */
    public static class CategoryItem {
        public final String key;
        public final String displayName;
        public final int iconResId;

        public CategoryItem(String key, String displayName, int iconResId) {
            this.key = key;
            this.displayName = displayName;
            this.iconResId = iconResId;
        }
    }

    /** Listener for category card clicks */
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem item);
    }

    private final List<CategoryItem> categories;
    private final OnCategoryClickListener listener;
    private final Context context;

    public CategoryAdapter(Context context, List<CategoryItem> categories,
                           OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Lab Topic: RecyclerView — inflating custom item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_card, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem item = categories.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * ViewHolder for a category card.
     *
     * Lab Topic: RecyclerView — ViewHolder pattern
     */
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView iconView;
        private final TextView nameView;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.category_card);
            iconView = itemView.findViewById(R.id.category_icon);
            nameView = itemView.findViewById(R.id.category_name);
        }

        void bind(CategoryItem item) {
            nameView.setText(item.displayName);
            iconView.setImageResource(item.iconResId);

            // TalkBack accessibility label
            cardView.setContentDescription(item.displayName);

            // Lab Topic: Explicit Intent — card click opens BoardActivity
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(item);
                }
            });
        }
    }
}
