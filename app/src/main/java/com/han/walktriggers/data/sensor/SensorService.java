package com.han.walktriggers.data.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorService {

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    public SensorService(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    }

    public float[] getLatLon() {
        float[] latLon = new float[2];

        return latLon;
    }
}
