package com.example.fitnesstrackerapp.ui.profile;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.utils.GoalPreferences;
import com.example.fitnesstrackerapp.utils.ThemePreferences;
import com.example.fitnesstrackerapp.utils.UserProfilePreferences;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class ProfileFragment extends Fragment {

    private FitnessViewModel viewModel;
    private ThemePreferences themePreferences;
    private UserProfilePreferences userProfilePreferences;

    private ImageView imgAvatar;
    private View layoutAvatarPicker;
    private TextInputEditText etProfileName;
    private TextInputEditText etProfileMotto;
    private MaterialButton btnSaveProfile;

    private TextInputEditText etStepGoal;
    private TextInputLayout tilStepGoal;

    private TextInputEditText etCalorieGoal;
    private TextInputLayout tilCalorieGoal;

    private TextInputEditText etWorkoutGoal;
    private TextInputLayout tilWorkoutGoal;

    private MaterialButton btnSaveGoals;

    private MaterialButtonToggleGroup toggleUnitSystem;
    private MaterialSwitch switchNotifications;
    private MaterialButtonToggleGroup toggleThemeMode;
    private MaterialButton btnResetAllData;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userProfilePreferences = new UserProfilePreferences(requireContext());

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String savedPath = userProfilePreferences.saveProfileImageFromUri(uri);
                if (savedPath != null && !savedPath.isEmpty()) {
                    File imageFile = new File(savedPath);
                    if (imageFile.exists()) {
                        imgAvatar.setImageURI(Uri.fromFile(imageFile));
                    } else {
                        imgAvatar.setImageURI(Uri.parse(savedPath));
                    }
                }
                Toast.makeText(getContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        themePreferences = new ThemePreferences(requireContext());

        loadUserProfile();
        loadGoals();
        setupPreferencesToggle();

        layoutAvatarPicker.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnSaveGoals.setOnClickListener(v -> saveGoals());
        btnResetAllData.setOnClickListener(v -> confirmResetData());
    }

    private void initViews(View view) {
        imgAvatar = view.findViewById(R.id.img_profile_avatar);
        layoutAvatarPicker = view.findViewById(R.id.layout_avatar_picker);
        etProfileName = view.findViewById(R.id.et_profile_name);
        etProfileMotto = view.findViewById(R.id.et_profile_motto);
        btnSaveProfile = view.findViewById(R.id.btn_save_profile);

        etStepGoal = view.findViewById(R.id.et_settings_step_goal);
        tilStepGoal = view.findViewById(R.id.til_settings_step_goal);

        etCalorieGoal = view.findViewById(R.id.et_settings_calorie_goal);
        tilCalorieGoal = view.findViewById(R.id.til_settings_calorie_goal);

        etWorkoutGoal = view.findViewById(R.id.et_settings_workout_goal);
        tilWorkoutGoal = view.findViewById(R.id.til_settings_workout_goal);

        btnSaveGoals = view.findViewById(R.id.btn_save_settings_goals);

        toggleUnitSystem = view.findViewById(R.id.toggle_unit_system);
        switchNotifications = view.findViewById(R.id.switch_notifications);
        toggleThemeMode = view.findViewById(R.id.toggle_theme_mode);
        btnResetAllData = view.findViewById(R.id.btn_reset_all_data);
    }

    private void loadUserProfile() {
        etProfileName.setText(userProfilePreferences.getUserName());
        etProfileMotto.setText(userProfilePreferences.getUserMotto());

        String uriStr = userProfilePreferences.getUserImageUri();
        if (uriStr != null && !uriStr.trim().isEmpty()) {
            File imageFile = new File(uriStr);
            if (imageFile.exists()) {
                imgAvatar.setImageURI(Uri.fromFile(imageFile));
            } else {
                try {
                    imgAvatar.setImageURI(Uri.parse(uriStr));
                } catch (Exception e) {
                    imgAvatar.setImageResource(R.drawable.ic_account_circle);
                }
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_account_circle);
        }
    }

    private void saveProfile() {
        String name = etProfileName.getText() != null ? etProfileName.getText().toString().trim() : "";
        String motto = etProfileMotto.getText() != null ? etProfileMotto.getText().toString().trim() : "";

        if (name.isEmpty()) {
            name = UserProfilePreferences.DEFAULT_NAME;
        }
        if (motto.isEmpty()) {
            motto = UserProfilePreferences.DEFAULT_MOTTO;
        }

        userProfilePreferences.setUserName(name);
        userProfilePreferences.setUserMotto(motto);

        Toast.makeText(requireContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show();
    }

    private void loadGoals() {
        GoalPreferences.FitnessGoals goals = viewModel.getCurrentGoals();
        if (goals != null) {
            etStepGoal.setText(String.valueOf(goals.stepGoal));
            etCalorieGoal.setText(String.valueOf(goals.calorieGoal));
            etWorkoutGoal.setText(String.valueOf(goals.workoutGoalMinutes));
        }
    }

    private void setupPreferencesToggle() {

        int currentUnit = userProfilePreferences.getUnitSystem();
        if (currentUnit == UserProfilePreferences.UNIT_IMPERIAL) {
            toggleUnitSystem.check(R.id.btn_unit_imperial);
        } else {
            toggleUnitSystem.check(R.id.btn_unit_metric);
        }

        toggleUnitSystem.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int unit = checkedId == R.id.btn_unit_imperial ? UserProfilePreferences.UNIT_IMPERIAL : UserProfilePreferences.UNIT_METRIC;
                userProfilePreferences.setUnitSystem(unit);
            }
        });


        switchNotifications.setChecked(userProfilePreferences.isNotificationsEnabled());
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                userProfilePreferences.setNotificationsEnabled(isChecked)
        );


        int currentTheme = themePreferences.getThemeMode();
        if (currentTheme == ThemePreferences.THEME_LIGHT) {
            toggleThemeMode.check(R.id.btn_theme_light);
        } else if (currentTheme == ThemePreferences.THEME_DARK) {
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

    private void confirmResetData() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_reset_title)
                .setMessage(R.string.confirm_reset_message)
                .setPositiveButton(R.string.reset_data, (dialog, which) -> {
                    viewModel.deleteAllActivities(() -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), R.string.data_reset_success, Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}