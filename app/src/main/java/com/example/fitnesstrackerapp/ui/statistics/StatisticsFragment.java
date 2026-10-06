package com.example.fitnesstrackerapp.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.ui.views.BarChartView;
import com.example.fitnesstrackerapp.utils.DateUtils;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StatisticsFragment extends Fragment {

    private FitnessViewModel viewModel;

    private TextView tvMotivationHeadline;
    private TextView tvMotivationSub;

    private TextView tvStepsTotal;
    private TextView tvCaloriesTotal;
    private TextView tvDurationTotal;
    private TextView tvWorkoutsTotal;

    private MaterialButtonToggleGroup togglePeriod;
    private BarChartView chartSteps;
    private BarChartView chartCalories;

    private int currentDaysPeriod = 7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupToggle();

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);

        chartSteps.setMetricType(BarChartView.MetricType.STEPS);
        chartCalories.setMetricType(BarChartView.MetricType.CALORIES);

        viewModel.getGoalsLiveData().observe(getViewLifecycleOwner(), goals -> {
            if (goals != null) {
                chartSteps.setTargetGoal(goals.stepGoal);
                chartCalories.setTargetGoal(goals.calorieGoal);
            }
        });

        loadPeriodData();
    }

    private void initViews(View view) {
        tvMotivationHeadline = view.findViewById(R.id.tv_motivation_headline);
        tvMotivationSub = view.findViewById(R.id.tv_motivation_sub);

        tvStepsTotal = view.findViewById(R.id.tv_stats_steps_total);
        tvCaloriesTotal = view.findViewById(R.id.tv_stats_calories_total);
        tvDurationTotal = view.findViewById(R.id.tv_stats_duration_total);
        tvWorkoutsTotal = view.findViewById(R.id.tv_stats_workouts_total);

        togglePeriod = view.findViewById(R.id.toggle_stats_period);
        chartSteps = view.findViewById(R.id.chart_stats_steps);
        chartCalories = view.findViewById(R.id.chart_stats_calories);
    }

    private void setupToggle() {
        togglePeriod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_period_7days) {
                    currentDaysPeriod = 7;
                } else if (checkedId == R.id.btn_period_30days) {
                    currentDaysPeriod = 30;
                } else if (checkedId == R.id.btn_period_90days) {
                    currentDaysPeriod = 90;
                }
                loadPeriodData();
            }
        });
    }

    private void loadPeriodData() {
        List<String> days = DateUtils.getLastNDays(currentDaysPeriod);
        String startDate = days.get(0);
        String endDate = days.get(days.size() - 1);

        viewModel.getActivitiesForDateRange(startDate, endDate).observe(getViewLifecycleOwner(), activities -> {
            processAndDisplayStatistics(days, activities);
        });
    }

    private void processAndDisplayStatistics(List<String> days, List<FitnessActivity> activities) {
        int totalSteps = 0;
        int totalCalories = 0;
        int totalDuration = 0;
        int workoutCount = 0;

        Set<String> activeDays = new HashSet<>();
        Map<String, Integer> dayStepsMap = new HashMap<>();
        Map<String, Integer> dayCaloriesMap = new HashMap<>();

        for (String day : days) {
            dayStepsMap.put(day, 0);
            dayCaloriesMap.put(day, 0);
        }

        if (activities != null) {
            workoutCount = activities.size();
            for (FitnessActivity act : activities) {
                totalSteps += act.getSteps();
                totalCalories += act.getCaloriesBurned();
                totalDuration += act.getDurationMinutes();
                activeDays.add(act.getDate());

                String d = act.getDate();
                if (dayStepsMap.containsKey(d)) {
                    int prevSteps = dayStepsMap.get(d) != null ? dayStepsMap.get(d) : 0;
                    dayStepsMap.put(d, prevSteps + act.getSteps());
                }
                if (dayCaloriesMap.containsKey(d)) {
                    int prevCals = dayCaloriesMap.get(d) != null ? dayCaloriesMap.get(d) : 0;
                    dayCaloriesMap.put(d, prevCals + act.getCaloriesBurned());
                }
            }
        }

        int activeDaysCount = activeDays.size();
        if (activeDaysCount > 0) {
            tvMotivationHeadline.setText(String.format(Locale.getDefault(), "You're on a roll! %d active %s", activeDaysCount, activeDaysCount == 1 ? "day" : "days"));
            tvMotivationSub.setText(String.format(Locale.getDefault(), "Great consistency! You burned %,d kcal in the last %d days.", totalCalories, currentDaysPeriod));
        } else {
            tvMotivationHeadline.setText("Ready to get moving?");
            tvMotivationSub.setText("Log your first workout to build your fitness streak!");
        }

        tvStepsTotal.setText(String.format(Locale.getDefault(), "%,d", totalSteps));
        tvCaloriesTotal.setText(String.format(Locale.getDefault(), "%,d", totalCalories));
        tvDurationTotal.setText(String.format(Locale.getDefault(), "%d min", totalDuration));
        tvWorkoutsTotal.setText(String.valueOf(workoutCount));

        // Build Steps & Calories Entries
        List<BarChartView.BarEntry> stepsEntries = new ArrayList<>();
        List<BarChartView.BarEntry> caloriesEntries = new ArrayList<>();

        for (String day : days) {
            String label;
            if (currentDaysPeriod == 7) {
                label = DateUtils.getDayOfWeekLabel(day);
            } else {
                label = DateUtils.getShortDateLabel(day);
            }

            String shortDate = DateUtils.getShortDateLabel(day);

            int sVal = dayStepsMap.containsKey(day) && dayStepsMap.get(day) != null ? dayStepsMap.get(day) : 0;
            int cVal = dayCaloriesMap.containsKey(day) && dayCaloriesMap.get(day) != null ? dayCaloriesMap.get(day) : 0;

            stepsEntries.add(new BarChartView.BarEntry(label, shortDate, sVal, day));
            caloriesEntries.add(new BarChartView.BarEntry(label, shortDate, cVal, day));
        }

        chartSteps.setEntries(stepsEntries);
        chartCalories.setEntries(caloriesEntries);
    }
}