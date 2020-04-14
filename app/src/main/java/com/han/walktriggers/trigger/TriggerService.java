package com.han.walktriggers.trigger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.han.walktriggers.TaskService;
import com.han.walktriggers.entity.TriggerInfo;
import com.han.walktriggers.utils.DateUtils;

public class TriggerService {
    private static final String TAG = "TriggerService";
    private Context mContext;
    private AlarmManager mAlarmManager;

    public TriggerService (Context context){
        this.mContext = context;
        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void addTrigger(TriggerInfo triggerInfo) {
        Intent intent = new Intent(mContext, TaskService.class);
        intent.setAction(triggerInfo.getTaskName());
        PendingIntent operation = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (triggerInfo.getStrParam1() != null) {
            intent.putExtra(TaskService.EXTRA_PARAM1, triggerInfo.getStrParam1());
        }

        if (triggerInfo.getIsAlarm() != null && triggerInfo.getIsAlarm()) {
            Log.d(TAG, "add " + triggerInfo.getTaskName() +" at: " + DateUtils.getDateString(triggerInfo.getTime()));
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerInfo.getTime(), operation);
        } else {
            Log.d(TAG, "add " + triggerInfo.getTaskName());
            mContext.startService(intent);
        }
    }
}
