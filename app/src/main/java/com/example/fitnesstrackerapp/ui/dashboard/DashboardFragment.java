package com.example.fitnesstrackerapp.ui.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.ui.addactivity.AddEditActivity;
import com.example.fitnesstrackerapp.ui.adapters.ActivityAdapter;
import com.example.fitnesstrackerapp.ui.details.ActivityDetailActivity;
import com.example.fitnesstrackerapp.ui.views.BarChartView;
import com.example.fitnesstrackerapp.utils.DateUtils;
import com.example.fitnesstrackerapp.utils.GoalPreferences;
import com.example.fitnesstrackerapp.utils.UserProfilePreferences;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashboardFragment extends Fragment implements ActivityAdapter.OnActivityClickListener {

    private FitnessViewModel viewModel;
    private ActivityAdapter recentAdapter;

    private TextView tvGreeting;
    private TextView tvTagline;
    private TextView tvHeaderDate;
    private ImageView imgAvatar;

    // Hero Card
    private TextView tvHeroStepsVal;
    private TextView tvHeroStepsGoal;
    private TextView tvHeroDistanceSub;
    private CircularProgressIndicator progressHeroCircle;

    // Today Cards
    private TextView tvStepsVal;
    private TextView tvCaloriesVal;
    private TextView tvDurationVal;
    private TextView tvDistanceVal;

    // Progress Bars
    private TextView tvProgressStepsText;
    private LinearProgressIndicator progressSteps;

    private TextView tvProgressCaloriesText;
    private LinearProgressIndicator progressCalories;

    private TextView tvProgressWorkoutText;
    private LinearProgressIndicator progressWorkout;

    // Chart
    private MaterialButtonToggleGroup toggleChartMetric;
    private BarChartView chartWeekly;
    private BarChartView.MetricType currentMetric = BarChartView.MetricType.STEPS;

    // List & Empty state
    private RecyclerView rvRecentActivities;
    private MaterialCardView cardEmptyState;
    private MaterialButton btnViewAllRecent;

    private GoalPreferences.FitnessGoals currentGoals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupGreeting();
        setupRecyclerView();
        setupListeners();

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupGreeting();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvTagline = view.findViewById(R.id.tv_tagline);
        tvHeaderDate = view.findViewById(R.id.tv_header_date);
        imgAvatar = view.findViewById(R.id.img_profile_avatar);

        tvHeroStepsVal = view.findViewById(R.id.tv_hero_steps_val);
        tvHeroStepsGoal = view.findViewById(R.id.tv_hero_steps_goal);
        tvHeroDistanceSub = view.findViewById(R.id.tv_hero_distance_sub);
        progressHeroCircle = view.findViewById(R.id.progress_hero_circle);

        tvStepsVal = view.findViewById(R.id.tv_steps_val);
        tvCaloriesVal = view.findViewById(R.id.tv_calories_val);
        tvDurationVal = view.findViewById(R.id.tv_duration_val);
        tvDistanceVal = view.findViewById(R.id.tv_distance_val);

        tvProgressStepsText = view.findViewById(R.id.tv_progress_steps_text);
        progressSteps = view.findViewById(R.id.progress_steps);

        tvProgressCaloriesText = view.findViewById(R.id.tv_progress_calories_text);
        progressCalories = view.findViewById(R.id.progress_calories);

        tvProgressWorkoutText = view.findViewById(R.id.tv_progress_workout_text);
        progressWorkout = view.findViewById(R.id.progress_workout);

        toggleChartMetric = view.findViewById(R.id.toggle_chart_metric);
        chartWeekly = view.findViewById(R.id.chart_dashboard_weekly);

        rvRecentActivities = view.findViewById(R.id.rv_dashboard_recent);
        cardEmptyState = view.findViewById(R.id.card_dashboard_empty_state);
        btnViewAllRecent = view.findViewById(R.id.btn_view_all_recent);

        MaterialButton btnAddFirst = view.findViewById(R.id.btn_dashboard_add_first);
        ExtendedFloatingActionButton fabAddActivity = view.findViewById(R.id.fab_add_activity);

        View btnNotification = view.findViewById(R.id.btn_notification);

        if (btnNotification != null) {
            btnNotification.setOnClickListener(v ->
                    Toast.makeText(getContext(), "You're all caught up on notifications!", Toast.LENGTH_SHORT).show()
            );
        }

        if (imgAvatar != null) {
            imgAvatar.setOnClickListener(v ->
                    Toast.makeText(getContext(), "FITTRACK Athlete Profile", Toast.LENGTH_SHORT).show()
            );
        }

        btnAddFirst.setOnClickListener(v -> openAddActivity());
        fabAddActivity.setOnClickListener(v -> openAddActivity());
    }

    private void setupGreeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = getString(R.string.greeting_morning);
        } else if (hour >= 12 && hour < 17) {
            greeting = getString(R.string.greeting_afternoon);
        } else {
            greeting = getString(R.string.greeting_evening);
        }

        UserProfilePreferences userPrefs = new UserProfilePreferences(requireContext());
        String name = userPrefs.getUserName();
        if (name != null && !name.trim().isEmpty() && !UserProfilePreferences.DEFAULT_NAME.equals(name)) {
            tvGreeting.setText(String.format("%s, %s", greeting, name.trim()));
        } else {
            tvGreeting.setText(greeting);
        }

        String motto = userPrefs.getUserMotto();
        if (motto != null && !motto.trim().isEmpty()) {
            tvTagline.setText(motto.trim());
        }

        String imageUriStr = userPrefs.getUserImageUri();
        if (imageUriStr != null && !imageUriStr.trim().isEmpty()) {
            try {
                imgAvatar.setImageURI(Uri.parse(imageUriStr));
            } catch (Exception e) {
                imgAvatar.setImageResource(R.drawable.ic_account_circle);
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_account_circle);
        }

        String todayDate = DateUtils.getTodayDateString();
        tvHeaderDate.setText(DateUtils.getDisplayDate(todayDate));
    }

    private void setupRecyclerView() {
        recentAdapter = new ActivityAdapter(this);
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentActivities.setAdapter(recentAdapter);
    }

    private void setupListeners() {
        btnViewAllRecent.setOnClickListener(v -> {
            if (getActivity() instanceof OnNavigateToHistoryListener) {
                ((OnNavigateToHistoryListener) getActivity()).onNavigateToHistory();
            }
        });

        toggleChartMetric.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_metric_steps) {
                    currentMetric = BarChartView.MetricType.STEPS;
                } else if (checkedId == R.id.btn_metric_calories) {
                    currentMetric = BarChartView.MetricType.CALORIES;
                } else if (checkedId == R.id.btn_metric_duration) {
                    currentMetric = BarChartView.MetricType.DURATION;
                }
                chartWeekly.setMetricType(currentMetric);
                updateChartGoal();
                loadWeeklyChartData();
            }
        });
    }

    public interface OnNavigateToHistoryListener {
        void onNavigateToHistory();
    }

    private void observeViewModel() {
        viewModel.getGoalsLiveData().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null) {
                currentGoals = goals;
                updateChartGoal();
                updateTodayProgressBars(viewModel.getTodayActivities().getValue());
            }
        });

        viewModel.getTodayActivities().observe(getViewLifecycleOwner(), activities -> {
            updateTodaySummaryCards(activities);
            updateTodayProgressBars(activities);
        });

        viewModel.getRecentActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities == null || activities.isEmpty()) {
                rvRecentActivities.setVisibility(View.GONE);
                cardEmptyState.setVisibility(View.VISIBLE);
            } else {
                rvRecentActivities.setVisibility(View.VISIBLE);
                cardEmptyState.setVisibility(View.GONE);
                recentAdapter.submitList(activities);
            }
        });

        List<String> last7Days = DateUtils.getLastNDays(7);
        String startDate = last7Days.get(0);
        String endDate = last7Days.get(6);

        viewModel.getActivitiesForDateRange(startDate, endDate).observe(getViewLifecycleOwner(), activities -> {
            processAndDisplayWeeklyChart(last7Days, activities);
        });
    }

    private void updateChartGoal() {
        if (currentGoals == null) return;
        switch (currentMetric) {
            case CALORIES:
                chartWeekly.setTargetGoal(currentGoals.calorieGoal);
                break;
            case DURATION:
                chartWeekly.setTargetGoal(currentGoals.workoutGoalMinutes);
                break;
            case STEPS:
            default:
                chartWeekly.setTargetGoal(currentGoals.stepGoal);
                break;
        }
    }

    private void updateTodaySummaryCards(List<FitnessActivity> activities) {
        int totalSteps = 0;
        int totalCalories = 0;
        int totalDuration = 0;

        if (activities != null) {
            for (FitnessActivity act : activities) {
                totalSteps += act.getSteps();
                totalCalories += act.getCaloriesBurned();
                totalDuration += act.getDurationMinutes();
            }
        }

        UserProfilePreferences userPrefs = new UserProfilePreferences(requireContext());
        boolean isImperial = userPrefs.getUnitSystem() == UserProfilePreferences.UNIT_IMPERIAL;

        double distanceKm = (totalSteps * 0.762) / 1000.0;
        double distanceValue = isImperial ? (distanceKm * 0.621371) : distanceKm;
        String distanceUnit = isImperial ? "mi" : "km";

        int stepGoal = currentGoals != null ? currentGoals.stepGoal : GoalPreferences.DEFAULT_STEP_GOAL;
        int stepPct = (int) Math.min(100, Math.round((totalSteps * 100.0) / Math.max(1, stepGoal)));

        tvHeroStepsVal.setText(String.format(Locale.getDefault(), "%,d", totalSteps));
        tvHeroStepsGoal.setText(String.format(Locale.getDefault(), "STEPS • Goal: %,d", stepGoal));
        tvHeroDistanceSub.setText(String.format(Locale.getDefault(), "📍 Distance: %.2f %s", distanceValue, distanceUnit));
        progressHeroCircle.setProgress(stepPct);

        tvStepsVal.setText(String.format(Locale.getDefault(), "%,d", totalSteps));
        tvCaloriesVal.setText(String.format(Locale.getDefault(), "%,d kcal", totalCalories));
        tvDurationVal.setText(String.format(Locale.getDefault(), "%d min", totalDuration));
        tvDistanceVal.setText(String.format(Locale.getDefault(), "%.2f %s", distanceValue, distanceUnit));
    }

    private void updateTodayProgressBars(List<FitnessActivity> activities) {
        int totalSteps = 0;
        int totalCalories = 0;
        int totalDuration = 0;

        if (activities != null) {
            for (FitnessActivity act : activities) {
                totalSteps += act.getSteps();
                totalCalories += act.getCaloriesBurned();
                totalDuration += act.getDurationMinutes();
            }
        }

        int stepGoal = currentGoals != null ? currentGoals.stepGoal : GoalPreferences.DEFAULT_STEP_GOAL;
        int calorieGoal = currentGoals != null ? currentGoals.calorieGoal : GoalPreferences.DEFAULT_CALORIE_GOAL;
        int workoutGoal = currentGoals != null ? currentGoals.workoutGoalMinutes : GoalPreferences.DEFAULT_WORKOUT_GOAL;

        int stepPct = (int) Math.min(100, Math.round((totalSteps * 100.0) / Math.max(1, stepGoal)));
        int calPct = (int) Math.min(100, Math.round((totalCalories * 100.0) / Math.max(1, calorieGoal)));
        int durPct = (int) Math.min(100, Math.round((totalDuration * 100.0) / Math.max(1, workoutGoal)));

        progressSteps.setProgress(stepPct);
        tvProgressStepsText.setText(String.format(Locale.getDefault(), "%,d / %,d steps (%d%%)", totalSteps, stepGoal, stepPct));

        progressCalories.setProgress(calPct);
        tvProgressCaloriesText.setText(String.format(Locale.getDefault(), "%,d / %,d kcal (%d%%)", totalCalories, calorieGoal, calPct));

        progressWorkout.setProgress(durPct);
        tvProgressWorkoutText.setText(String.format(Locale.getDefault(), "%d / %d min (%d%%)", totalDuration, workoutGoal, durPct));
    }

    private void loadWeeklyChartData() {
        List<String> last7Days = DateUtils.getLastNDays(7);
        String startDate = last7Days.get(0);
        String endDate = last7Days.get(6);

        viewModel.getActivitiesForDateRange(startDate, endDate).observe(getViewLifecycleOwner(), activities -> {
            processAndDisplayWeeklyChart(last7Days, activities);
        });
    }

    private void processAndDisplayWeeklyChart(List<String> days, List<FitnessActivity> activities) {
        Map<String, Float> dayValuesMap = new HashMap<>();
        for (String day : days) {
            dayValuesMap.put(day, 0f);
        }

        if (activities != null) {
            for (FitnessActivity act : activities) {
                String d = act.getDate();
                if (dayValuesMap.containsKey(d)) {
                    float val = dayValuesMap.get(d) != null ? dayValuesMap.get(d) : 0f;
                    switch (currentMetric) {
                        case CALORIES:
                            val += act.getCaloriesBurned();
                            break;
                        case DURATION:
                            val += act.getDurationMinutes();
                            break;
                        case STEPS:
                        default:
                            val += act.getSteps();
                            break;
                    }
                    dayValuesMap.put(d, val);
                }
            }
        }

        List<BarChartView.BarEntry> entries = new ArrayList<>();
        for (String day : days) {
            String label = DateUtils.getDayOfWeekLabel(day);
            String shortDate = DateUtils.getShortDateLabel(day);
            float val = dayValuesMap.containsKey(day) && dayValuesMap.get(day) != null ? dayValuesMap.get(day) : 0f;
            entries.add(new BarChartView.BarEntry(label, shortDate, val, day));
        }

        chartWeekly.setEntries(entries);
    }

    private void openAddActivity() {
        Intent intent = new Intent(requireContext(), AddEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityClick(FitnessActivity activity) {
        Intent intent = new Intent(requireContext(), ActivityDetailActivity.class);
        intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY_ID, activity.getId());
        startActivity(intent);
    }

    @Override
    public void onActivityMoreClick(FitnessActivity activity, View anchorView) {
        PopupMenu popup = new PopupMenu(requireContext(), anchorView);
        popup.getMenu().add("Edit");
        popup.getMenu().add("Delete");

        popup.setOnMenuItemClickListener(item -> {
            if ("Edit".equals(item.getTitle())) {
                Intent intent = new Intent(requireContext(), AddEditActivity.class);
                intent.putExtra(AddEditActivity.EXTRA_ACTIVITY_ID, activity.getId());
                startActivity(intent);
                return true;
            } else if ("Delete".equals(item.getTitle())) {
                showDeleteConfirmationDialog(activity);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showDeleteConfirmationDialog(FitnessActivity activity) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteActivity(activity, () -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), R.string.activity_deleted, Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}