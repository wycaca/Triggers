package com.han.walktriggers.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public final static String DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final String TAG = "DateUtils";

    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    public static Date dateStrToDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage() + " check dateStr");
        }
        return date;
    }

    public static boolean isToday(Date date) {
        Date today = new Date();
        return isSameDay(today, date);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if(date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static Date getDayStart(Date date) {
        if(date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // end of a day 12:00:00
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            date = calendar.getTime();
        }
        return date;
    }

    public static Date getDayEnd(Date date) {
        if(date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // end of a day 23:59:59
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            date = calendar.getTime();
        }
        return date;
    }

    public static long getHourToday(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 20);
        if(hour >= 0 && hour <= 23) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
        } else {
            Log.e(TAG,"input hour is not correct!");
            calendar.set(Calendar.HOUR_OF_DAY, 9);
        }
        Date time = calendar.getTime();
        return time.getTime();
    }

    public static String getDateString(long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
        return dateFormat.format(date);
    }

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(DateUtils.getDayStart(date));
        System.out.println(DateUtils.getDayEnd(date));
    }
}
