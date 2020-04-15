package com.han.walktriggers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.han.walktriggers.data.source.WeatherService;
import com.han.walktriggers.entity.TriggerInfo;
import com.han.walktriggers.trigger.TriggerService;

import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    private static final int CODE = 233;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check sensor Permission
        boolean hasPermission = checkLocationPermission();
        Log.d(TAG, "hasPermission? :" + hasPermission);
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
        triggerInfo = new TriggerInfo();
        triggerInfo.setTaskName(TaskService.ACTION_CHECK_PROGRESS);
        triggerInfo.setTime(19);
        triggerService.addTrigger(triggerInfo);

        // check history at 22:00
        triggerInfo = new TriggerInfo();
        triggerInfo.setTaskName(TaskService.ACTION_CHECK_HISTORY);
        triggerInfo.setTime(22);
        triggerService.addTrigger(triggerInfo);

        // goal suggestion at 17:00
        triggerInfo = new TriggerInfo();
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

    private boolean checkLocationPermission() {
        boolean permissionAccess =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
        if (!permissionAccess) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION}, CODE);
            Toast.makeText(this, "Please allow Permission", Toast.LENGTH_SHORT).show();
        } else {
            WeatherService weatherService = new WeatherService(this);
            weatherService.getNewestWeather();
            addTriggers();
        }
        return permissionAccess;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == CODE) {
//            boolean hasPermission = false;
//            for (int i = 0; i < grantResults.length; i++) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    hasPermission = true;
//                } else {
//                    hasPermission = false;
//                    break;
//                }
//            }
//            if (hasPermission) {
//                WeatherService weatherService = new WeatherService(this);
//                weatherService.getNewestWeather();
//                addTriggers();
//            } else {
//                // Permission Denied
//                Toast.makeText(this, "Please allow Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
