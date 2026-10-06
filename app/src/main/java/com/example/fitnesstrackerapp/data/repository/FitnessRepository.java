package com.example.fitnesstrackerapp.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.fitnesstrackerapp.data.dao.FitnessActivityDao;
import com.example.fitnesstrackerapp.data.database.AppDatabase;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FitnessRepository {

    private final FitnessActivityDao fitnessActivityDao;
    private final ExecutorService executorService;

    public interface RepositoryCallback<T> {
        void onResult(T result);
    }

    public FitnessRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.fitnessActivityDao = database.fitnessActivityDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insertActivity(final FitnessActivity activity, final RepositoryCallback<Long> callback) {
        executorService.execute(() -> {
            long newId = fitnessActivityDao.insert(activity);
            if (callback != null) {
                callback.onResult(newId);
            }
        });
    }

    public void updateActivity(final FitnessActivity activity, final Runnable onComplete) {
        executorService.execute(() -> {
            fitnessActivityDao.update(activity);
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void deleteActivity(final FitnessActivity activity, final Runnable onComplete) {
        executorService.execute(() -> {
            fitnessActivityDao.delete(activity);
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void deleteAllActivities(final Runnable onComplete) {
        executorService.execute(() -> {
            fitnessActivityDao.deleteAllActivities();
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public LiveData<FitnessActivity> getActivityById(long id) {
        return fitnessActivityDao.getActivityById(id);
    }

    public LiveData<List<FitnessActivity>> getAllActivities() {
        return fitnessActivityDao.getAllActivities();
    }

    public LiveData<List<FitnessActivity>> getRecentActivities(int limit) {
        return fitnessActivityDao.getRecentActivities(limit);
    }

    public LiveData<List<FitnessActivity>> getActivitiesForDate(String dateString) {
        return fitnessActivityDao.getActivitiesForDate(dateString);
    }

    public LiveData<List<FitnessActivity>> getActivitiesForDateRange(String startDate, String endDate) {
        return fitnessActivityDao.getActivitiesForDateRange(startDate, endDate);
    }

    public void getActivitiesForDateRangeSync(final String startDate, final String endDate, final RepositoryCallback<List<FitnessActivity>> callback) {
        executorService.execute(() -> {
            List<FitnessActivity> list = fitnessActivityDao.getActivitiesForDateRangeSync(startDate, endDate);
            if (callback != null) {
                callback.onResult(list);
            }
        });
    }
}