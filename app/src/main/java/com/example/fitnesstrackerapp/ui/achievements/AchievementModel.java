package com.example.fitnesstrackerapp.ui.achievements;

public class AchievementModel {

    private final String title;
    private final String description;
    private final boolean isUnlocked;
    private final String progressText;
    private final int progressPct;

    public AchievementModel(String title, String description, boolean isUnlocked, String progressText, int progressPct) {
        this.title = title;
        this.description = description;
        this.isUnlocked = isUnlocked;
        this.progressText = progressText;
        this.progressPct = progressPct;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public String getProgressText() {
        return progressText;
    }

    public int getProgressPct() {
        return progressPct;
    }
}