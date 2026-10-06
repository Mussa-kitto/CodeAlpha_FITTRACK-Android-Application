package com.example.fitnesstrackerapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private static final String DATE_FORMAT_ISO = "yyyy-MM-dd";
    private static final String DATE_FORMAT_DISPLAY = "MMM d, yyyy";
    private static final String DATE_FORMAT_DAY_OF_WEEK = "EEE";
    private static final String DATE_FORMAT_SHORT = "MMM d";

    public static String getTodayDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String formatDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
        return sdf.format(date);
    }

    public static String getDisplayDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        String today = getTodayDateString();
        if (isoDate.equals(today)) {
            return "Today";
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdfIso = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
        String yesterday = sdfIso.format(cal.getTime());

        if (isoDate.equals(yesterday)) {
            return "Yesterday";
        }

        try {
            Date date = sdfIso.parse(isoDate);
            if (date != null) {
                SimpleDateFormat sdfDisplay = new SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault());
                return sdfDisplay.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isoDate;
    }

    public static String getDayOfWeekLabel(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            SimpleDateFormat sdfIso = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
            Date date = sdfIso.parse(isoDate);
            if (date != null) {
                SimpleDateFormat sdfDow = new SimpleDateFormat(DATE_FORMAT_DAY_OF_WEEK, Locale.getDefault());
                return sdfDow.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getShortDateLabel(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "";
        try {
            SimpleDateFormat sdfIso = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
            Date date = sdfIso.parse(isoDate);
            if (date != null) {
                SimpleDateFormat sdfShort = new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.getDefault());
                return sdfShort.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isoDate;
    }

    public static List<String> getLastNDays(int n) {
        List<String> days = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // We want the last n days leading up to today, in chronological order
        cal.add(Calendar.DAY_OF_YEAR, -(n - 1));
        for (int i = 0; i < n; i++) {
            days.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return days;
    }

    public static long parseIsoDateToMillis(String isoDate) {
        try {
            SimpleDateFormat sdfIso = new SimpleDateFormat(DATE_FORMAT_ISO, Locale.getDefault());
            Date date = sdfIso.parse(isoDate);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
}