package com.example.fitnesstrackerapp.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.ui.achievements.AchievementModel;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private final List<AchievementModel> list = new ArrayList<>();

    public void submitList(List<AchievementModel> newList) {
        this.list.clear();
        if (newList != null) {
            this.list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvStatus;
        private final TextView tvDesc;
        private final LinearProgressIndicator progress;
        private final TextView tvProgressText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_achievement_title);
            tvStatus = itemView.findViewById(R.id.tv_achievement_status);
            tvDesc = itemView.findViewById(R.id.tv_achievement_desc);
            progress = itemView.findViewById(R.id.progress_achievement);
            tvProgressText = itemView.findViewById(R.id.tv_achievement_progress_text);
        }

        public void bind(AchievementModel model) {
            tvTitle.setText(model.getTitle());
            tvDesc.setText(model.getDescription());
            progress.setProgress(model.getProgressPct());
            tvProgressText.setText(model.getProgressText());

            if (model.isUnlocked()) {
                tvStatus.setText("UNLOCKED ✓");
                tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#DCF2E8")));
                tvStatus.setTextColor(Color.parseColor("#123B2A"));
            } else {
                tvStatus.setText("LOCKED");
                tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFF2EB")));
                tvStatus.setTextColor(Color.parseColor("#718078"));
            }
        }
    }
}