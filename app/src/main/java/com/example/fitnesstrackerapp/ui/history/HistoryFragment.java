package com.example.fitnesstrackerapp.ui.history;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.fitnesstrackerapp.ui.adapters.HistoryAdapter;
import com.example.fitnesstrackerapp.ui.adapters.HistoryListItem;
import com.example.fitnesstrackerapp.ui.details.ActivityDetailActivity;
import com.example.fitnesstrackerapp.utils.DateUtils;
import com.example.fitnesstrackerapp.viewmodel.FitnessViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment implements ActivityAdapter.OnActivityClickListener {

    private FitnessViewModel viewModel;
    private HistoryAdapter adapter;

    private TextView tvSummarySub;
    private AutoCompleteTextView actvFilter;
    private RecyclerView rvHistory;
    private MaterialCardView cardEmptyState;

    private List<FitnessActivity> allActivitiesList = new ArrayList<>();
    private String selectedFilter = "All Activities";

    private static final String[] FILTER_OPTIONS = new String[]{
            "All Activities", "Walking", "Running", "Cycling", "Swimming",
            "Strength Training", "Gym Workout", "Yoga",
            "Cardio", "Football", "Basketball", "Other"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupFilterDropdown();

        viewModel = new ViewModelProvider(requireActivity()).get(FitnessViewModel.class);
        observeViewModel();
    }

    private void initViews(View view) {
        tvSummarySub = view.findViewById(R.id.tv_history_summary_sub);
        actvFilter = view.findViewById(R.id.actv_history_filter);
        rvHistory = view.findViewById(R.id.rv_history_list);
        cardEmptyState = view.findViewById(R.id.card_history_empty_state);

        MaterialButton btnAddFirst = view.findViewById(R.id.btn_history_add_first);
        btnAddFirst.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(this);
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(adapter);
    }

    private void setupFilterDropdown() {
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                FILTER_OPTIONS
        );
        actvFilter.setAdapter(filterAdapter);

        actvFilter.setOnItemClickListener((parent, view, position, id) -> {
            selectedFilter = FILTER_OPTIONS[position];
            applyFilter();
        });
    }

    private void observeViewModel() {
        viewModel.getAllActivities().observe(getViewLifecycleOwner(), activities -> {
            if (activities != null) {
                allActivitiesList = activities;
                applyFilter();
                updateSummaryText(activities);
            }
        });
    }

    private void applyFilter() {
        List<FitnessActivity> filteredList = new ArrayList<>();
        if ("All Activities".equals(selectedFilter)) {
            filteredList.addAll(allActivitiesList);
        } else {
            for (FitnessActivity act : allActivitiesList) {
                if (selectedFilter.equals(act.getExerciseType())) {
                    filteredList.add(act);
                }
            }
        }

        if (filteredList.isEmpty()) {
            rvHistory.setVisibility(View.GONE);
            cardEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvHistory.setVisibility(View.VISIBLE);
            cardEmptyState.setVisibility(View.GONE);

            List<HistoryListItem> groupedItems = buildGroupedList(filteredList);
            adapter.submitList(groupedItems);
        }
    }

    private List<HistoryListItem> buildGroupedList(List<FitnessActivity> activities) {
        List<HistoryListItem> list = new ArrayList<>();
        String todayStr = DateUtils.getTodayDateString();

        String currentGroup = "";

        for (FitnessActivity act : activities) {
            String actDate = act.getDate();
            String displayGroup;

            if (todayStr.equals(actDate)) {
                displayGroup = "Today";
            } else if (DateUtils.getDisplayDate(actDate).equals("Yesterday")) {
                displayGroup = "Yesterday";
            } else {
                displayGroup = "Earlier";
            }

            if (!displayGroup.equals(currentGroup)) {
                currentGroup = displayGroup;
                list.add(new HistoryListItem(currentGroup));
            }
            list.add(new HistoryListItem(act));
        }

        return list;
    }

    private void updateSummaryText(List<FitnessActivity> activities) {
        int totalWorkouts = activities.size();
        int totalCalories = 0;
        int totalMinutes = 0;

        for (FitnessActivity act : activities) {
            totalCalories += act.getCaloriesBurned();
            totalMinutes += act.getDurationMinutes();
        }

        tvSummarySub.setText(String.format(Locale.getDefault(),
                "%d logged workouts • %,d kcal • %,d min total",
                totalWorkouts, totalCalories, totalMinutes));
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