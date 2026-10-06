package com.example.fitnesstrackerapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final List<HistoryListItem> items = new ArrayList<>();
    private final ActivityAdapter.OnActivityClickListener listener;

    public HistoryAdapter(ActivityAdapter.OnActivityClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<HistoryListItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position).getItemType() == HistoryListItem.ItemType.HEADER) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fitness_activity, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HistoryListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.getHeaderTitle());
        } else if (holder instanceof ItemViewHolder && item.getActivity() != null) {
            ((ItemViewHolder) holder).bind(item.getActivity(), listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_section_header_title);
        }

        public void bind(String title) {
            tvTitle.setText(title);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgIcon;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvDuration;
        private final TextView tvCalories;
        private final TextView tvSteps;
        private final ImageButton btnMore;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_exercise_icon);
            tvTitle = itemView.findViewById(R.id.tv_exercise_title);
            tvSubtitle = itemView.findViewById(R.id.tv_activity_subtitle);
            tvDuration = itemView.findViewById(R.id.tv_stat_duration);
            tvCalories = itemView.findViewById(R.id.tv_stat_calories);
            tvSteps = itemView.findViewById(R.id.tv_stat_steps);
            btnMore = itemView.findViewById(R.id.btn_more_options);
        }

        public void bind(final FitnessActivity activity, final ActivityAdapter.OnActivityClickListener listener) {
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

            imgIcon.setImageResource(getExerciseIconRes(activity.getExerciseType()));

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