package com.example.fitnesstrackerapp.ui.details;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.ui.addactivity.AddEditActivity;
import com.example.fitnesstrackerapp.utils.DateUtils;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class ActivityDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ACTIVITY_ID = "extra_activity_id";

    private FitnessViewModel viewModel;
    private long activityId = -1;
    private FitnessActivity currentActivity;

    private ImageView imgIcon;
    private TextView tvTitle;
    private TextView tvDate;
    private TextView tvDuration;
    private TextView tvCalories;
    private TextView tvSteps;
    private TextView tvCreatedAt;
    private TextView tvNotes;
    private MaterialCardView cardNotes;

    private MaterialButton btnEdit;
    private MaterialButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        setupToolbar();

        viewModel = new ViewModelProvider(this).get(FitnessViewModel.class);

        if (getIntent().hasExtra(EXTRA_ACTIVITY_ID)) {
            activityId = getIntent().getLongExtra(EXTRA_ACTIVITY_ID, -1);
            if (activityId != -1) {
                loadActivityDetails();
            } else {
                finish();
            }
        } else {
            finish();
        }

        btnEdit.setOnClickListener(v -> editActivity());
        btnDelete.setOnClickListener(v -> confirmDeleteActivity());
    }

    private void initViews() {
        imgIcon = findViewById(R.id.img_detail_exercise_icon);
        tvTitle = findViewById(R.id.tv_detail_exercise_title);
        tvDate = findViewById(R.id.tv_detail_date);

        tvDuration = findViewById(R.id.tv_detail_duration);
        tvCalories = findViewById(R.id.tv_detail_calories);
        tvSteps = findViewById(R.id.tv_detail_steps);
        tvCreatedAt = findViewById(R.id.tv_detail_created_at);

        tvNotes = findViewById(R.id.tv_detail_notes);
        cardNotes = findViewById(R.id.card_detail_notes);

        btnEdit = findViewById(R.id.btn_detail_edit);
        btnDelete = findViewById(R.id.btn_detail_delete);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadActivityDetails() {
        viewModel.getActivityById(activityId).observe(this, activity -> {
            if (activity != null) {
                currentActivity = activity;
                displayActivityData(activity);
            }
        });
    }

    private void displayActivityData(FitnessActivity activity) {
        tvTitle.setText(activity.getExerciseType());
        tvDate.setText(DateUtils.getDisplayDate(activity.getDate()));

        tvDuration.setText(String.format(Locale.getDefault(), "%d min", activity.getDurationMinutes()));
        tvCalories.setText(String.format(Locale.getDefault(), "%d kcal", activity.getCaloriesBurned()));

        if (activity.getSteps() > 0) {
            tvSteps.setText(String.format(Locale.getDefault(), "%,d", activity.getSteps()));
        } else {
            tvSteps.setText("0");
        }

        tvCreatedAt.setText(DateUtils.getShortDateLabel(activity.getDate()));

        String notes = activity.getNotes();
        if (notes != null && !notes.trim().isEmpty()) {
            cardNotes.setVisibility(View.VISIBLE);
            tvNotes.setText(notes.trim());
        } else {
            cardNotes.setVisibility(View.GONE);
        }

        imgIcon.setImageResource(getExerciseIconRes(activity.getExerciseType()));
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

    private void editActivity() {
        if (currentActivity != null) {
            Intent intent = new Intent(this, AddEditActivity.class);
            intent.putExtra(AddEditActivity.EXTRA_ACTIVITY_ID, currentActivity.getId());
            startActivity(intent);
        }
    }

    private void confirmDeleteActivity() {
        if (currentActivity == null) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteActivity(currentActivity, () -> runOnUiThread(() -> {
                        Toast.makeText(this, R.string.activity_deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    }));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}