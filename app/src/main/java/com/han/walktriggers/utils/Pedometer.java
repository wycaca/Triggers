package com.han.walktriggers.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class Pedometer implements SensorEventListener {

    private final static String TAG = "Pedometer";
    private Context mContext;

    private Integer stepCounter = 0;
    MutableLiveData<Integer> stepNum = new MutableLiveData<>();

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public Pedometer(Context mContext){
        this.mContext = mContext;
        //check
        Log.i(TAG, "Has sensor? " + isKitkatWithStepSensor());

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        stepCounter += sensorEvent.values.length;
        setStepNum(stepCounter);
        Log.e(TAG, "add step: " + stepCounter);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // get step number.
    public void stepCounterSwitch(Boolean isStart) {
        if (null == mSensorManager){
            mSensorManager = (SensorManager) mContext.getSystemService(Activity.SENSOR_SERVICE);
        }
        if (null == mSensor) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            Log.e(TAG, "Can not find the step sensor!!!");
        } else {
            Log.i(TAG, "Get the Sensor: " + mSensor.getName());
        }

        if (isStart) {
            register();
        } else {
            unRegister();
        }
    }

    public LiveData<Integer> getStepNum () {
        return stepNum;
    }

    private void setStepNum (int num) {
        stepNum.postValue(num);
    }

    private void register() {
        // start count again
        stepCounter = 0;
        boolean flag = mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        if (flag) {
            Toast.makeText(mContext, "Start Sensor!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Start Sensor");
        } else {
            Toast.makeText(mContext, "Start Listener Failed", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Start Listener Failed");
        }
    }

    private void unRegister() {
        mSensorManager.unregisterListener(this);
        Log.i(TAG, "Stop Sensor");
        if (mSensorManager != null) {
            mSensorManager = null;
        }
        if (mSensor != null) {
            mSensor = null;
        }
        Log.e(TAG, "total add step: " + stepCounter);
    }

    private boolean isKitkatWithStepSensor() {
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        PackageManager packageManager = mContext.getPackageManager();
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
//                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
    }
}

