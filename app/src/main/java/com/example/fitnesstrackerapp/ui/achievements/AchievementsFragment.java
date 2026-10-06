package com.example.fitnesstrackerapp.ui.achievements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.ui.adapters.AchievementAdapter;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AchievementsFragment extends Fragment {

    private FitnessViewModel viewModel;
    private AchievementAdapter adapter;
    private RecyclerView rvAchievements;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAchievements = view.findViewById(R.id.rv_achievements);
        adapter = new AchievementAdapter();
        rvAchievements.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAchievements.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), this::calculateAchievements);
    }

    private void calculateAchievements(List<FitnessActivity> activities) {
        int totalWorkouts = activities != null ? activities.size() : 0;
        int totalCalories = 0;
        int totalSteps = 0;
        int maxSingleDaySteps = 0;

        Set<String> activeDays = new HashSet<>();
        Map<String, Integer> dayStepsMap = new HashMap<>();

        if (activities != null) {
            for (FitnessActivity act : activities) {
                totalCalories += act.getCaloriesBurned();
                totalSteps += act.getSteps();
                activeDays.add(act.getDate());

                String d = act.getDate();
                int currentDaySteps = (dayStepsMap.containsKey(d) && dayStepsMap.get(d) != null ? dayStepsMap.get(d) : 0) + act.getSteps();
                dayStepsMap.put(d, currentDaySteps);

                if (currentDaySteps > maxSingleDaySteps) {
                    maxSingleDaySteps = currentDaySteps;
                }
            }
        }

        double totalDistanceKm = (totalSteps * 0.762) / 1000.0;
        int activeDaysCount = activeDays.size();

        List<AchievementModel> list = new ArrayList<>();

        // 1. First Workout
        boolean a1Unlocked = totalWorkouts >= 1;
        int a1Pct = a1Unlocked ? 100 : 0;
        list.add(new AchievementModel(
                "🏆 First Workout",
                "Complete your first fitness activity.",
                a1Unlocked,
                String.format(Locale.getDefault(), "%d / 1", Math.min(1, totalWorkouts)),
                a1Pct
        ));

        // 2. 1,000 Calories Burned
        boolean a2Unlocked = totalCalories >= 1000;
        int a2Pct = Math.min(100, (int) Math.round((totalCalories * 100.0) / 1000.0));
        list.add(new AchievementModel(
                "🔥 1,000 Calories Burned",
                "Burn a total of 1,000 kcal through active workouts.",
                a2Unlocked,
                String.format(Locale.getDefault(), "%,d / 1,000 kcal", totalCalories),
                a2Pct
        ));

        // 3. 10K Steps Day
        boolean a3Unlocked = maxSingleDaySteps >= 10000;
        int a3Pct = Math.min(100, (int) Math.round((maxSingleDaySteps * 100.0) / 10000.0));
        list.add(new AchievementModel(
                "👟 10K Steps Day",
                "Reach 10,000 steps in a single day.",
                a3Unlocked,
                String.format(Locale.getDefault(), "%,d / 10,000 steps max", maxSingleDaySteps),
                a3Pct
        ));

        // 4. 7-Day Workout Streak
        boolean a4Unlocked = activeDaysCount >= 7;
        int a4Pct = Math.min(100, (int) Math.round((activeDaysCount * 100.0) / 7.0));
        list.add(new AchievementModel(
                "💪 7-Day Active Streak",
                "Log fitness activities across 7 distinct days.",
                a4Unlocked,
                String.format(Locale.getDefault(), "%d / 7 days", activeDaysCount),
                a4Pct
        ));

        // 5. 5 KM Distance Total
        boolean a5Unlocked = totalDistanceKm >= 5.0;
        int a5Pct = Math.min(100, (int) Math.round((totalDistanceKm * 100.0) / 5.0));
        list.add(new AchievementModel(
                "🏃 5 KM Distance Total",
                "Cover a total of 5.0 km through steps or workouts.",
                a5Unlocked,
                String.format(Locale.getDefault(), "%.1f / 5.0 km", totalDistanceKm),
                a5Pct
        ));

        adapter.submitList(list);
    }
}