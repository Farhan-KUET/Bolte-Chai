package com.farhan.boltechai.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.farhan.boltechai.BolteChaiApp;
import com.farhan.boltechai.R;
import com.farhan.boltechai.data.model.PictogramResult;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for Pictogram search results.
 */
public class PictogramAdapter extends RecyclerView.Adapter<PictogramAdapter.PictogramViewHolder> {

    private List<PictogramResult> list = new ArrayList<>();
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PictogramResult pictogram);
    }

    public PictogramAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void submitList(List<PictogramResult> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PictogramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pictogram, parent, false);
        return new PictogramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictogramViewHolder holder, int position) {
        PictogramResult pictogram = list.get(position);
        holder.bind(pictogram, context, listener);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class PictogramViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public PictogramViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_pictogram);
            textView = itemView.findViewById(R.id.text_pictogram_label);
        }

        public void bind(PictogramResult pictogram, Context context, OnItemClickListener listener) {
            String lang = BolteChaiApp.getInstance().getCurrentLanguage();
            textView.setText(pictogram.getLabel(lang));

            // Load image using Glide with disk caching
            Glide.with(context)
                 .load(pictogram.getImageUrl())
                 .placeholder(R.drawable.ic_placeholder)
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .into(imageView);

            itemView.setOnClickListener(v -> listener.onItemClick(pictogram));
        }
    }
}
