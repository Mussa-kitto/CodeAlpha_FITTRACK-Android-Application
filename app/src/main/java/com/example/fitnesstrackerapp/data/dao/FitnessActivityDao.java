package com.example.fitnesstrackerapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fitnesstrackerapp.data.entity.FitnessActivity;

import java.util.List;

@Dao
public interface FitnessActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FitnessActivity activity);

    @Update
    void update(FitnessActivity activity);

    @Delete
    void delete(FitnessActivity activity);

    @Query("DELETE FROM fitness_activities")
    void deleteAllActivities();

    @Query("SELECT * FROM fitness_activities WHERE id = :id")
    LiveData<FitnessActivity> getActivityById(long id);

    @Query("SELECT * FROM fitness_activities WHERE id = :id")
    FitnessActivity getActivityByIdSync(long id);

    @Query("SELECT * FROM fitness_activities ORDER BY timestamp DESC, id DESC")
    LiveData<List<FitnessActivity>> getAllActivities();

    @Query("SELECT * FROM fitness_activities ORDER BY timestamp DESC, id DESC LIMIT :limit")
    LiveData<List<FitnessActivity>> getRecentActivities(int limit);

    @Query("SELECT * FROM fitness_activities WHERE date = :dateString ORDER BY timestamp DESC")
    LiveData<List<FitnessActivity>> getActivitiesForDate(String dateString);

    @Query("SELECT * FROM fitness_activities WHERE date = :dateString ORDER BY timestamp DESC")
    List<FitnessActivity> getActivitiesForDateSync(String dateString);

    @Query("SELECT * FROM fitness_activities WHERE date >= :startDateString AND date <= :endDateString ORDER BY date ASC, timestamp ASC")
    LiveData<List<FitnessActivity>> getActivitiesForDateRange(String startDateString, String endDateString);

    @Query("SELECT * FROM fitness_activities WHERE date >= :startDateString AND date <= :endDateString ORDER BY date ASC, timestamp ASC")
    List<FitnessActivity> getActivitiesForDateRangeSync(String startDateString, String endDateString);
}