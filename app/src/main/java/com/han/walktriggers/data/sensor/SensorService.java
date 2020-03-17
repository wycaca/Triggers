package com.han.walktriggers.data.sensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.math.BigDecimal;

import static android.content.Context.MODE_PRIVATE;

public class SensorService {
    private static final String TAG = "sensorService";

    private Context mContext;
    private SensorManager mSensorManager;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sp;
    private Editor editor;

    public SensorService(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        sp = mContext.getSharedPreferences("location", MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setLocationSp() {
        Log.d(TAG, "get location");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(mContext.getMainExecutor() , new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // save 0.00 and protect user privacy
                            BigDecimal bg = new BigDecimal(location.getLatitude());
                            float latitude = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

                            bg = new BigDecimal(location.getLongitude());
                            float longitude = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

                            Log.d(TAG, "latitude: " + latitude + ", longitude: " + longitude);

                            editor.putFloat("lat", latitude);
                            editor.putFloat("lon", longitude);
                            editor.apply();
                        }
                    }
                });
    }
}
