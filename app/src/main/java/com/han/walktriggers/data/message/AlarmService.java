package com.han.walktriggers.data.message;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.han.walktriggers.TaskService;
import com.han.walktriggers.utils.DateUtils;

public class AlarmService {
    private AlarmManager mAlarmManager;
    private Context mContext;
    private static final String TAG = "alarmService";

    public AlarmService(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void addWeatherTask() {
        Intent intent = new Intent(mContext, TaskService.class);
        intent.setAction(TaskService.ACTION_WEATHER);
        PendingIntent operation = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Long times = DateUtils.getHourToday(9);
        // for test
        times = System.currentTimeMillis() + 20 * 1000;
        Log.d(TAG, "add the weather task at: " + DateUtils.getDateString(times));
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, times, operation);
    }
}
