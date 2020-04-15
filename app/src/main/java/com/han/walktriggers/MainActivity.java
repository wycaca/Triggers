package com.han.walktriggers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.han.walktriggers.data.source.WeatherService;
import com.han.walktriggers.entity.TriggerInfo;
import com.han.walktriggers.trigger.TriggerService;

import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private static final int PHYISCAL_ACTIVITY = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check sensor Permission
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }

        WeatherService weatherService = new WeatherService(this);
        weatherService.getNewestWeather();

        addTriggers();
    }

    private void addTriggers() {
        TriggerService triggerService = new TriggerService(this);

        // weather trigger
        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setTaskName(TaskService.ACTION_WEATHER);
        // for test
//        Long times = System.currentTimeMillis() + 10 * 1000;
//        triggerInfo.setTime(times);

        // check weather at 9:00
        triggerInfo.setTime(9);
        triggerService.addTrigger(triggerInfo);

        // check progress trigger at 19:00
        triggerInfo.setTaskName(TaskService.ACTION_CHECK_PROGRESS);
        triggerInfo.setTime(19);
//        triggerService.addTrigger(triggerInfo);

        // check history at 22:00
        triggerInfo.setTaskName(TaskService.ACTION_CHECK_HISTORY);
        triggerInfo.setTime(22);
        triggerService.addTrigger(triggerInfo);

        // goal suggestion at 17:00
        triggerInfo.setTaskName(TaskService.ACTION_SUGGEST_GOAL);
        triggerInfo.setTime(17);
        triggerService.addTrigger(triggerInfo);

        // wifi ssid check
        // do not need task time, auto check when network state is changed
        triggerInfo = new TriggerInfo();
        triggerInfo.setTaskName(TaskService.ACTION_CHECK_WIFI_SSID);
        triggerService.addTrigger(triggerInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.finish();
    }
}
