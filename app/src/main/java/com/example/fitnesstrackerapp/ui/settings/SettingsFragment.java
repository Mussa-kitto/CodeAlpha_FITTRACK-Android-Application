package com.example.fitnesstrackerapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.utils.GoalPreferences;
import com.example.fitnesstrackerapp.utils.ThemePreferences;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SettingsFragment extends Fragment {

    private FitnessViewModel viewModel;
    private ThemePreferences themePreferences;

    private TextInputEditText etStepGoal;
    private TextInputLayout tilStepGoal;

    private TextInputEditText etCalorieGoal;
    private TextInputLayout tilCalorieGoal;

    private TextInputEditText etWorkoutGoal;
    private TextInputLayout tilWorkoutGoal;

    private MaterialButton btnSaveGoals;
    private MaterialButtonToggleGroup toggleThemeMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        themePreferences = new ThemePreferences(requireContext());

        GoalPreferences.FitnessGoals goals = viewModel.getCurrentGoals();
        if (goals != null) {
            etStepGoal.setText(String.valueOf(goals.stepGoal));
            etCalorieGoal.setText(String.valueOf(goals.calorieGoal));
            etWorkoutGoal.setText(String.valueOf(goals.workoutGoalMinutes));
        }

        setupThemeToggle();

        btnSaveGoals.setOnClickListener(v -> saveGoals());
    }

    private void initViews(View view) {
        etStepGoal = view.findViewById(R.id.et_settings_step_goal);
        tilStepGoal = view.findViewById(R.id.til_settings_step_goal);

        etCalorieGoal = view.findViewById(R.id.et_settings_calorie_goal);
        tilCalorieGoal = view.findViewById(R.id.til_settings_calorie_goal);

        etWorkoutGoal = view.findViewById(R.id.et_settings_workout_goal);
        tilWorkoutGoal = view.findViewById(R.id.til_settings_workout_goal);

        btnSaveGoals = view.findViewById(R.id.btn_save_settings_goals);
        toggleThemeMode = view.findViewById(R.id.toggle_theme_mode);
    }

    private void setupThemeToggle() {
        int currentMode = themePreferences.getThemeMode();
        if (currentMode == ThemePreferences.THEME_LIGHT) {
            toggleThemeMode.check(R.id.btn_theme_light);
        } else if (currentMode == ThemePreferences.THEME_DARK) {
            toggleThemeMode.check(R.id.btn_theme_dark);
        } else {
            toggleThemeMode.check(R.id.btn_theme_system);
        }

        toggleThemeMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int mode = ThemePreferences.THEME_SYSTEM;
                if (checkedId == R.id.btn_theme_light) {
                    mode = ThemePreferences.THEME_LIGHT;
                } else if (checkedId == R.id.btn_theme_dark) {
                    mode = ThemePreferences.THEME_DARK;
                }
                themePreferences.saveThemeMode(mode);
            }
        });
    }

    private void saveGoals() {
        tilStepGoal.setError(null);
        tilCalorieGoal.setError(null);
        tilWorkoutGoal.setError(null);

        String stepStr = etStepGoal.getText() != null ? etStepGoal.getText().toString().trim() : "";
        String calStr = etCalorieGoal.getText() != null ? etCalorieGoal.getText().toString().trim() : "";
        String workoutStr = etWorkoutGoal.getText() != null ? etWorkoutGoal.getText().toString().trim() : "";

        boolean isValid = true;

        int stepGoal = GoalPreferences.DEFAULT_STEP_GOAL;
        if (stepStr.isEmpty()) {
            tilStepGoal.setError("Step goal cannot be empty");
            isValid = false;
        } else {
            try {
                stepGoal = Integer.parseInt(stepStr);
                if (stepGoal <= 0) {
                    tilStepGoal.setError("Step goal must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilStepGoal.setError("Invalid number");
                isValid = false;
            }
        }

        int calGoal = GoalPreferences.DEFAULT_CALORIE_GOAL;
        if (calStr.isEmpty()) {
            tilCalorieGoal.setError("Calorie goal cannot be empty");
            isValid = false;
        } else {
            try {
                calGoal = Integer.parseInt(calStr);
                if (calGoal <= 0) {
                    tilCalorieGoal.setError("Calorie goal must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilCalorieGoal.setError("Invalid number");
                isValid = false;
            }
        }

        int workoutGoal = GoalPreferences.DEFAULT_WORKOUT_GOAL;
        if (workoutStr.isEmpty()) {
            tilWorkoutGoal.setError("Workout goal cannot be empty");
            isValid = false;
        } else {
            try {
                workoutGoal = Integer.parseInt(workoutStr);
                if (workoutGoal <= 0) {
                    tilWorkoutGoal.setError("Workout goal must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilWorkoutGoal.setError("Invalid number");
                isValid = false;
            }
        }

        if (!isValid) return;

        viewModel.saveGoals(stepGoal, calGoal, workoutGoal);
        Toast.makeText(requireContext(), R.string.goals_updated, Toast.LENGTH_SHORT).show();
    }
}