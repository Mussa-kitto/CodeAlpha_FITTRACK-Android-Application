package com.example.fitnesstrackerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GoalPreferences {

    private static final String PREF_NAME = "fitness_goals_pref";
    private static final String KEY_STEP_GOAL = "key_step_goal";
    private static final String KEY_CALORIE_GOAL = "key_calorie_goal";
    private static final String KEY_WORKOUT_GOAL = "key_workout_goal";

    public static final int DEFAULT_STEP_GOAL = 10000;
    public static final int DEFAULT_CALORIE_GOAL = 500;
    public static final int DEFAULT_WORKOUT_GOAL = 60;

    private final SharedPreferences prefs;
    private final MutableLiveData<FitnessGoals> goalsLiveData = new MutableLiveData<>();

    public static class FitnessGoals {
        public final int stepGoal;
        public final int calorieGoal;
        public final int workoutGoalMinutes;

        public FitnessGoals(int stepGoal, int calorieGoal, int workoutGoalMinutes) {
            this.stepGoal = stepGoal;
            this.calorieGoal = calorieGoal;
            this.workoutGoalMinutes = workoutGoalMinutes;
        }
    }

    public GoalPreferences(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadGoals();
    }

    private void loadGoals() {
        int steps = prefs.getInt(KEY_STEP_GOAL, DEFAULT_STEP_GOAL);
        int calories = prefs.getInt(KEY_CALORIE_GOAL, DEFAULT_CALORIE_GOAL);
        int workout = prefs.getInt(KEY_WORKOUT_GOAL, DEFAULT_WORKOUT_GOAL);
        goalsLiveData.setValue(new FitnessGoals(steps, calories, workout));
    }

    public LiveData<FitnessGoals> getGoalsLiveData() {
        return goalsLiveData;
    }

    public FitnessGoals getGoals() {
        int steps = prefs.getInt(KEY_STEP_GOAL, DEFAULT_STEP_GOAL);
        int calories = prefs.getInt(KEY_CALORIE_GOAL, DEFAULT_CALORIE_GOAL);
        int workout = prefs.getInt(KEY_WORKOUT_GOAL, DEFAULT_WORKOUT_GOAL);
        return new FitnessGoals(steps, calories, workout);
    }

    public void saveGoals(int stepGoal, int calorieGoal, int workoutGoalMinutes) {
        prefs.edit()
                .putInt(KEY_STEP_GOAL, stepGoal)
                .putInt(KEY_CALORIE_GOAL, calorieGoal)
                .putInt(KEY_WORKOUT_GOAL, workoutGoalMinutes)
                .apply();
        goalsLiveData.setValue(new FitnessGoals(stepGoal, calorieGoal, workoutGoalMinutes));
    }
}