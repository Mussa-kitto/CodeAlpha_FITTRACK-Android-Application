package com.example.fitnesstrackerapp.ui.addactivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.utils.DateUtils;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_ACTIVITY_ID = "extra_activity_id";

    private FitnessViewModel viewModel;
    private long editingActivityId = -1;
    private FitnessActivity existingActivity;

    private View coordinatorLayout;
    private ChipGroup chipGroupExercise;

    private TextInputEditText etDuration;
    private TextInputLayout tilDuration;

    private TextInputEditText etCalories;
    private TextInputLayout tilCalories;

    private TextInputEditText etSteps;
    private TextInputLayout tilSteps;

    private TextInputEditText etDate;
    private TextInputLayout tilDate;

    private TextInputEditText etNotes;
    private MaterialButton btnSave;

    private String selectedIsoDate;
    private String selectedExerciseType = "Running";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        initViews();
        setupToolbar();
        setupChips();
        setupDatePicker();

        viewModel = new ViewModelProvider(this).get(FitnessViewModel.class);

        if (getIntent().hasExtra(EXTRA_ACTIVITY_ID)) {
            editingActivityId = getIntent().getLongExtra(EXTRA_ACTIVITY_ID, -1);
            if (editingActivityId != -1) {
                loadExistingActivity();
            }
        } else {
            selectedIsoDate = DateUtils.getTodayDateString();
            etDate.setText(DateUtils.getDisplayDate(selectedIsoDate));
            if (getIntent().hasExtra("exercise_type")) {
                String prefilledType = getIntent().getStringExtra("exercise_type");
                selectChipForExerciseType(prefilledType);
            }
        }

        btnSave.setOnClickListener(v -> saveActivity());
    }

    private void initViews() {
        coordinatorLayout = findViewById(R.id.coordinator_add_edit);
        chipGroupExercise = findViewById(R.id.chip_group_exercise);

        etDuration = findViewById(R.id.et_duration);
        tilDuration = findViewById(R.id.til_duration);

        etCalories = findViewById(R.id.et_calories);
        tilCalories = findViewById(R.id.til_calories);

        etSteps = findViewById(R.id.et_steps);
        tilSteps = findViewById(R.id.til_steps);

        etDate = findViewById(R.id.et_date);
        tilDate = findViewById(R.id.til_date);

        etNotes = findViewById(R.id.et_notes);
        btnSave = findViewById(R.id.btn_save_activity);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_add_edit);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupChips() {
        chipGroupExercise.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int chipId = checkedIds.get(0);
                Chip chip = findViewById(chipId);
                if (chip != null) {
                    selectedExerciseType = chip.getText().toString();
                }
            }
        });
    }

    private void selectChipForExerciseType(String type) {
        if (type == null) return;
        int targetChipId = R.id.chip_running;
        switch (type) {
            case "Walking":
                targetChipId = R.id.chip_walking;
                break;
            case "Cycling":
                targetChipId = R.id.chip_cycling;
                break;
            case "Gym Workout":
            case "Strength Training":
                targetChipId = R.id.chip_gym;
                break;
            case "Swimming":
                targetChipId = R.id.chip_swimming;
                break;
            case "Yoga":
                targetChipId = R.id.chip_yoga;
                break;
            case "Cardio":
                targetChipId = R.id.chip_cardio;
                break;
            case "Football":
                targetChipId = R.id.chip_football;
                break;
            case "Basketball":
                targetChipId = R.id.chip_basketball;
                break;
            case "Other":
                targetChipId = R.id.chip_other;
                break;
            case "Running":
            default:
                targetChipId = R.id.chip_running;
                break;
        }
        chipGroupExercise.check(targetChipId);
        selectedExerciseType = type;
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (selectedIsoDate != null) {
                cal.setTimeInMillis(DateUtils.parseIsoDateToMillis(selectedIsoDate));
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String isoDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                selectedYear, selectedMonth + 1, selectedDay);
                        selectedIsoDate = isoDate;
                        etDate.setText(DateUtils.getDisplayDate(isoDate));
                        tilDate.setError(null);
                    },
                    year, month, day
            );
            dpd.show();
        });
    }

    private void loadExistingActivity() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.edit_activity);
        }
        btnSave.setText(R.string.update_activity);

        viewModel.getActivityById(editingActivityId).observe(this, activity -> {
            if (activity != null) {
                existingActivity = activity;
                selectChipForExerciseType(activity.getExerciseType());
                etDuration.setText(String.valueOf(activity.getDurationMinutes()));
                etCalories.setText(String.valueOf(activity.getCaloriesBurned()));
                etSteps.setText(activity.getSteps() > 0 ? String.valueOf(activity.getSteps()) : "");
                selectedIsoDate = activity.getDate();
                etDate.setText(DateUtils.getDisplayDate(selectedIsoDate));
                etNotes.setText(activity.getNotes() != null ? activity.getNotes() : "");
            }
        });
    }

    private void saveActivity() {
        tilDuration.setError(null);
        tilCalories.setError(null);
        tilSteps.setError(null);
        tilDate.setError(null);

        String durationStr = etDuration.getText() != null ? etDuration.getText().toString().trim() : "";
        String caloriesStr = etCalories.getText() != null ? etCalories.getText().toString().trim() : "";
        String stepsStr = etSteps.getText() != null ? etSteps.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        boolean isValid = true;

        if (selectedExerciseType == null || selectedExerciseType.isEmpty()) {
            Toast.makeText(this, R.string.err_exercise_type_required, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        int duration = 0;
        if (durationStr.isEmpty()) {
            tilDuration.setError(getString(R.string.err_duration_invalid));
            isValid = false;
        } else {
            try {
                duration = Integer.parseInt(durationStr);
                if (duration <= 0) {
                    tilDuration.setError(getString(R.string.err_duration_invalid));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilDuration.setError(getString(R.string.err_duration_invalid));
                isValid = false;
            }
        }

        int calories = 0;
        if (caloriesStr.isEmpty()) {
            tilCalories.setError(getString(R.string.err_calories_invalid));
            isValid = false;
        } else {
            try {
                calories = Integer.parseInt(caloriesStr);
                if (calories < 0) {
                    tilCalories.setError(getString(R.string.err_calories_invalid));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilCalories.setError(getString(R.string.err_calories_invalid));
                isValid = false;
            }
        }

        int steps = 0;
        if (!stepsStr.isEmpty()) {
            try {
                steps = Integer.parseInt(stepsStr);
                if (steps < 0) {
                    tilSteps.setError(getString(R.string.err_steps_invalid));
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilSteps.setError(getString(R.string.err_steps_invalid));
                isValid = false;
            }
        }

        if (selectedIsoDate == null || selectedIsoDate.isEmpty()) {
            tilDate.setError(getString(R.string.err_date_required));
            isValid = false;
        }

        if (!isValid) return;

        long timestamp = DateUtils.parseIsoDateToMillis(selectedIsoDate);

        if (editingActivityId != -1 && existingActivity != null) {
            existingActivity.setExerciseType(selectedExerciseType);
            existingActivity.setDurationMinutes(duration);
            existingActivity.setCaloriesBurned(calories);
            existingActivity.setSteps(steps);
            existingActivity.setDate(selectedIsoDate);
            existingActivity.setTimestamp(timestamp);
            existingActivity.setNotes(notes);

            viewModel.updateActivity(existingActivity, () -> runOnUiThread(() -> {
                Snackbar.make(coordinatorLayout, R.string.activity_updated, Snackbar.LENGTH_SHORT).show();
                btnSave.postDelayed(this::finish, 600);
            }));
        } else {
            FitnessActivity newActivity = new FitnessActivity(
                    selectedExerciseType,
                    duration,
                    calories,
                    steps,
                    selectedIsoDate,
                    timestamp,
                    notes
            );

            viewModel.insertActivity(newActivity, id -> runOnUiThread(() -> {
                Snackbar.make(coordinatorLayout, R.string.activity_saved, Snackbar.LENGTH_SHORT).show();
                btnSave.postDelayed(this::finish, 600);
            }));
        }
    }
}