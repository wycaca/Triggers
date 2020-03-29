package com.han.walktriggers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.han.walktriggers.data.message.AlarmService;
import com.han.walktriggers.data.source.WeatherService;

import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private static final int PHYISCAL_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check Permission
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }

        WeatherService weatherService = new WeatherService(this);
        weatherService.getNewestWeather();

        AlarmService alarmService = new AlarmService(this);
        alarmService.addWeatherTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.finish();
    }
}
