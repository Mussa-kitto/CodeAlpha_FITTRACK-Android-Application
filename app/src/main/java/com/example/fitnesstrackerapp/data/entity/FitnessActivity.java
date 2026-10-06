package com.example.fitnesstrackerapp.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "fitness_activities")
public class FitnessActivity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String exerciseType;
    private int durationMinutes;
    private int caloriesBurned;
    private int steps;
    private String date; // Format: YYYY-MM-DD
    private long timestamp;
    private String notes;
    private long createdAt;

    public FitnessActivity(String exerciseType, int durationMinutes, int caloriesBurned, int steps, String date, long timestamp, String notes) {
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.steps = steps;
        this.date = date;
        this.timestamp = timestamp;
        this.notes = notes;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}