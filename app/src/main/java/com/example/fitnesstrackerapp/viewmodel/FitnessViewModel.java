package com.example.fitnesstrackerapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fitnesstrackerapp.data.entity.FitnessActivity;
import com.example.fitnesstrackerapp.data.repository.FitnessRepository;
import com.example.fitnesstrackerapp.utils.DateUtils;
import com.example.fitnesstrackerapp.utils.GoalPreferences;

import java.util.List;

public class FitnessViewModel extends AndroidViewModel {

    private final FitnessRepository repository;
    private final GoalPreferences goalPreferences;

    private final LiveData<List<FitnessActivity>> todayActivities;
    private final LiveData<List<FitnessActivity>> recentActivities;
    private final LiveData<List<FitnessActivity>> allActivities;
    private final LiveData<GoalPreferences.FitnessGoals> goalsLiveData;

    public FitnessViewModel(@NonNull Application application) {
        super(application);
        repository = new FitnessRepository(application);
        goalPreferences = new GoalPreferences(application);

        String today = DateUtils.getTodayDateString();
        todayActivities = repository.getActivitiesForDate(today);
        recentActivities = repository.getRecentActivities(5);
        allActivities = repository.getAllActivities();
        goalsLiveData = goalPreferences.getGoalsLiveData();
    }

    public LiveData<List<FitnessActivity>> getTodayActivities() {
        return todayActivities;
    }

    public LiveData<List<FitnessActivity>> getRecentActivities() {
        return recentActivities;
    }

    public LiveData<List<FitnessActivity>> getAllActivities() {
        return allActivities;
    }

    public LiveData<GoalPreferences.FitnessGoals> getGoalsLiveData() {
        return goalsLiveData;
    }

    public GoalPreferences.FitnessGoals getCurrentGoals() {
        return goalPreferences.getGoals();
    }

    public void saveGoals(int stepGoal, int calorieGoal, int workoutGoalMinutes) {
        goalPreferences.saveGoals(stepGoal, calorieGoal, workoutGoalMinutes);
    }

    public void insertActivity(FitnessActivity activity, FitnessRepository.RepositoryCallback<Long> callback) {
        repository.insertActivity(activity, callback);
    }

    public void updateActivity(FitnessActivity activity, Runnable onComplete) {
        repository.updateActivity(activity, onComplete);
    }

    public void deleteActivity(FitnessActivity activity, Runnable onComplete) {
        repository.deleteActivity(activity, onComplete);
    }

    public void deleteAllActivities(Runnable onComplete) {
        repository.deleteAllActivities(onComplete);
    }

    public LiveData<FitnessActivity> getActivityById(long id) {
        return repository.getActivityById(id);
    }

    public LiveData<List<FitnessActivity>> getActivitiesForDateRange(String startDate, String endDate) {
        return repository.getActivitiesForDateRange(startDate, endDate);
    }
}