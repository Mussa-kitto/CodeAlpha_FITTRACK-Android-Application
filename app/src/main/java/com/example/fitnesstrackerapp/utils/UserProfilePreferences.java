package com.example.fitnesstrackerapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UserProfilePreferences {

    private static final String PREF_NAME = "fittrack_user_profile_pref";

    private static final String KEY_NAME = "key_user_name";
    private static final String KEY_MOTTO = "key_user_motto";
    private static final String KEY_IMAGE_URI = "key_user_image_uri";
    private static final String KEY_UNIT_SYSTEM = "key_unit_system";
    private static final String KEY_NOTIFICATIONS = "key_notifications";

    public static final String DEFAULT_NAME = "FITTRACK Athlete";
    public static final String DEFAULT_MOTTO = "Small steps. Stronger you.";
    public static final int UNIT_METRIC = 0;
    public static final int UNIT_IMPERIAL = 1;

    private final SharedPreferences prefs;
    private final Context context;

    public UserProfilePreferences(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, DEFAULT_NAME);
    }

    public void setUserName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    public String getUserMotto() {
        return prefs.getString(KEY_MOTTO, DEFAULT_MOTTO);
    }

    public void setUserMotto(String motto) {
        prefs.edit().putString(KEY_MOTTO, motto).apply();
    }

    public String getUserImageUri() {
        return prefs.getString(KEY_IMAGE_URI, "");
    }

    public void setUserImageUri(String uri) {
        prefs.edit().putString(KEY_IMAGE_URI, uri).apply();
    }

    public String saveProfileImageFromUri(Uri sourceUri) {
        if (sourceUri == null) return "";
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return "";

            File destFile = new File(context.getFilesDir(), "profile_avatar.jpg");
            OutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            String savedPath = destFile.getAbsolutePath();
            setUserImageUri(savedPath);
            return savedPath;
        } catch (Exception e) {
            e.printStackTrace();
            String uriStr = sourceUri.toString();
            setUserImageUri(uriStr);
            return uriStr;
        }
    }

    public int getUnitSystem() {
        return prefs.getInt(KEY_UNIT_SYSTEM, UNIT_METRIC);
    }

    public void setUnitSystem(int unitSystem) {
        prefs.edit().putInt(KEY_UNIT_SYSTEM, unitSystem).apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }
}