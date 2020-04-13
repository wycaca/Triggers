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

        TriggerService triggerService = new TriggerService(this);

        // weather trigger
        TriggerInfo triggerInfo = new TriggerInfo();
        triggerInfo.setTaskName(TaskService.ACTION_WEATHER);
        // for test
        Long times = System.currentTimeMillis() + 12 * 1000;
        triggerInfo.setTime(times);
//        triggerService.addTrigger(triggerInfo);

        // check progress trigger
        triggerInfo.setTaskName(TaskService.ACTION_CHECK_PROGRESS);
//        times = System.currentTimeMillis() + 24 * 1000;
        triggerInfo.setTime(times);
        triggerService.addTrigger(triggerInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.finish();
    }
}
