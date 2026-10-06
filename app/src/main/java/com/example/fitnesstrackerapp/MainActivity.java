package com.example.fitnesstrackerapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fitnesstrackerapp.ui.achievements.AchievementsFragment;
import com.example.fitnesstrackerapp.ui.dashboard.DashboardFragment;
import com.example.fitnesstrackerapp.ui.profile.ProfileFragment;
import com.example.fitnesstrackerapp.ui.statistics.StatisticsFragment;
import com.example.fitnesstrackerapp.ui.workouts.WorkoutsFragment;
import com.example.fitnesstrackerapp.utils.ThemePreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements DashboardFragment.OnNavigateToHistoryListener {

    private BottomNavigationView bottomNavigationView;

    private final Fragment dashboardFragment = new DashboardFragment();
    private final Fragment statisticsFragment = new StatisticsFragment();
    private final Fragment workoutsFragment = new WorkoutsFragment();
    private final Fragment achievementsFragment = new AchievementsFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemePreferences(this).applyThemeFromPreferences();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initFragments();
        setupBottomNavigation();
    }

    private void initFragments() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "5").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, achievementsFragment, "4").hide(achievementsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, workoutsFragment, "3").hide(workoutsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, statisticsFragment, "2").hide(statisticsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, dashboardFragment, "1").commit();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(dashboardFragment);
                return true;
            } else if (itemId == R.id.nav_progress) {
                switchFragment(statisticsFragment);
                return true;
            } else if (itemId == R.id.nav_workouts) {
                switchFragment(workoutsFragment);
                return true;
            } else if (itemId == R.id.nav_achievements) {
                switchFragment(achievementsFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment fragment) {
        if (activeFragment != fragment) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(fragment)
                    .commit();
            activeFragment = fragment;
        }
    }

    @Override
    public void onNavigateToHistory() {
        switchFragment(workoutsFragment);
        bottomNavigationView.setSelectedItemId(R.id.nav_workouts);
    }
}