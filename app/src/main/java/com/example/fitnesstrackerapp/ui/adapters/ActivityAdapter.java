package com.example.fitnesstrackerapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.utils.DateUtils;

import java.util.Locale;

public class ActivityAdapter extends ListAdapter<FitnessActivity, ActivityAdapter.ActivityViewHolder> {

    public interface OnActivityClickListener {
        void onActivityClick(FitnessActivity activity);
        void onActivityMoreClick(FitnessActivity activity, View anchorView);
    }

    private final OnActivityClickListener listener;

    public ActivityAdapter(OnActivityClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<FitnessActivity> DIFF_CALLBACK = new DiffUtil.ItemCallback<FitnessActivity>() {
        @Override
        public boolean areItemsTheSame(@NonNull FitnessActivity oldItem, @NonNull FitnessActivity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FitnessActivity oldItem, @NonNull FitnessActivity newItem) {
            return oldItem.getDurationMinutes() == newItem.getDurationMinutes() &&
                    oldItem.getCaloriesBurned() == newItem.getCaloriesBurned() &&
                    oldItem.getSteps() == newItem.getSteps() &&
                    oldItem.getExerciseType().equals(newItem.getExerciseType()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    (oldItem.getNotes() == null ? newItem.getNotes() == null : oldItem.getNotes().equals(newItem.getNotes()));
        }
    };

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        FitnessActivity activity = getItem(position);
        holder.bind(activity, listener);
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgIcon;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvDuration;
        private final TextView tvCalories;
        private final TextView tvSteps;
        private final ImageButton btnMore;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_exercise_icon);
            tvTitle = itemView.findViewById(R.id.tv_exercise_title);
            tvSubtitle = itemView.findViewById(R.id.tv_activity_subtitle);
            tvDuration = itemView.findViewById(R.id.tv_stat_duration);
            tvCalories = itemView.findViewById(R.id.tv_stat_calories);
            tvSteps = itemView.findViewById(R.id.tv_stat_steps);
            btnMore = itemView.findViewById(R.id.btn_more_options);
        }

        public void bind(final FitnessActivity activity, final OnActivityClickListener listener) {
            tvTitle.setText(activity.getExerciseType());

            String displayDate = DateUtils.getDisplayDate(activity.getDate());
            String notes = activity.getNotes();
            if (notes != null && !notes.trim().isEmpty()) {
                tvSubtitle.setText(String.format("%s • %s", displayDate, notes.trim()));
            } else {
                tvSubtitle.setText(displayDate);
            }

            tvDuration.setText(String.format(Locale.getDefault(), "%d min", activity.getDurationMinutes()));
            tvCalories.setText(String.format(Locale.getDefault(), "%d kcal", activity.getCaloriesBurned()));

            if (activity.getSteps() > 0) {
                tvSteps.setVisibility(View.VISIBLE);
                tvSteps.setText(String.format(Locale.getDefault(), "%,d steps", activity.getSteps()));
            } else {
                tvSteps.setVisibility(View.GONE);
            }

            // Set exercise icon based on type
            int iconRes = getExerciseIconRes(activity.getExerciseType());
            imgIcon.setImageResource(iconRes);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActivityClick(activity);
                }
            });

            btnMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActivityMoreClick(activity, btnMore);
                }
            });
        }

        private int getExerciseIconRes(String type) {
            if (type == null) return R.drawable.ic_directions_run;
            switch (type) {
                case "Walking":
                    return R.drawable.ic_directions_walk;
                case "Running":
                    return R.drawable.ic_directions_run;
                case "Cycling":
                    return R.drawable.ic_directions_bike;
                case "Swimming":
                    return R.drawable.ic_pool;
                case "Strength Training":
                case "Gym Workout":
                    return R.drawable.ic_fitness_center;
                case "Yoga":
                    return R.drawable.ic_self_improvement;
                case "Football":
                    return R.drawable.ic_sports_soccer;
                case "Basketball":
                    return R.drawable.ic_sports_basketball;
                case "Cardio":
                case "Other":
                default:
                    return R.drawable.ic_directions_run;
            }
        }
    }
}