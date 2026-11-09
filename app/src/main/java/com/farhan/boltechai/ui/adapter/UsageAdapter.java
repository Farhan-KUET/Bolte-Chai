package com.farhan.boltechai.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farhan.boltechai.R;
import com.farhan.boltechai.data.db.entity.UsageEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for usage history.
 * 
 * Lab Topic: RecyclerView with custom adapter and ViewHolder
 */
public class UsageAdapter extends RecyclerView.Adapter<UsageAdapter.UsageViewHolder> {

    private List<UsageEntity> usageList = new ArrayList<>();
    private int maxCount = 0;

    public void submitList(List<UsageEntity> newUsageList) {
        this.usageList = newUsageList != null ? newUsageList : new ArrayList<>();
        this.maxCount = 0;
        if (!this.usageList.isEmpty()) {
            for (UsageEntity entity : this.usageList) {
                if (entity.getUseCount() > maxCount) {
                    maxCount = entity.getUseCount();
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage, parent, false);
        return new UsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageViewHolder holder, int position) {
        UsageEntity usage = usageList.get(position);
        holder.bind(usage, maxCount);
    }

    @Override
    public int getItemCount() {
        return usageList.size();
    }

    static class UsageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgTile;
        private final TextView tvLabel;
        private final TextView tvCount;
        private final ProgressBar progressBar;

        public UsageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTile = itemView.findViewById(R.id.img_usage_tile);
            tvLabel = itemView.findViewById(R.id.text_usage_label);
            tvCount = itemView.findViewById(R.id.text_usage_count);
            progressBar = itemView.findViewById(R.id.progress_usage);
        }

        public void bind(UsageEntity usage, int maxCount) {
            tvLabel.setText(usage.getLabelBn());
            tvCount.setText(String.valueOf(usage.getUseCount()));
            
            imgTile.setImageResource(R.drawable.ic_placeholder);
            progressBar.setMax(Math.max(maxCount, 1));
            progressBar.setProgress(usage.getUseCount());
        }
    }
}
