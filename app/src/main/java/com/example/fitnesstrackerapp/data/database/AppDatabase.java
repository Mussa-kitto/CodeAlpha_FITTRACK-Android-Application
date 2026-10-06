package com.example.fitnesstrackerapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fitnesstrackerapp.data.dao.FitnessActivityDao;
import com.example.fitnesstrackerapp.data.entity.FitnessActivity;

@Database(entities = {FitnessActivity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "fitness_tracker_db";
    private static volatile AppDatabase INSTANCE;

    public abstract FitnessActivityDao fitnessActivityDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}