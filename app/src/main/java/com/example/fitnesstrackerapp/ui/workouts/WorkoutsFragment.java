package com.example.fitnesstrackerapp.ui.workouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitnesstrackerapp.R;
import com.example.fitnesstrackerapp.ui.addactivity.AddEditActivity;

public class WorkoutsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_start_running).setOnClickListener(v -> startWorkout("Running"));
        view.findViewById(R.id.btn_start_walking).setOnClickListener(v -> startWorkout("Walking"));
        view.findViewById(R.id.btn_start_cycling).setOnClickListener(v -> startWorkout("Cycling"));
        view.findViewById(R.id.btn_start_strength).setOnClickListener(v -> startWorkout("Gym Workout"));
        view.findViewById(R.id.btn_start_yoga).setOnClickListener(v -> startWorkout("Yoga"));

        view.findViewById(R.id.card_workout_running).setOnClickListener(v -> startWorkout("Running"));
        view.findViewById(R.id.card_workout_walking).setOnClickListener(v -> startWorkout("Walking"));
        view.findViewById(R.id.card_workout_cycling).setOnClickListener(v -> startWorkout("Cycling"));
        view.findViewById(R.id.card_workout_strength).setOnClickListener(v -> startWorkout("Gym Workout"));
        view.findViewById(R.id.card_workout_yoga).setOnClickListener(v -> startWorkout("Yoga"));
    }

    private void startWorkout(String exerciseType) {
        Intent intent = new Intent(requireContext(), AddEditActivity.class);
        intent.putExtra("exercise_type", exerciseType);
        startActivity(intent);
    }
}