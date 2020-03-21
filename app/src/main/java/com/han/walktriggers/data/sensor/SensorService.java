package com.han.walktriggers.data.sensor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.han.walktriggers.data.AppDataBase;
import com.han.walktriggers.data.entity.UserInfo;

import java.math.BigDecimal;

import static android.content.Context.MODE_PRIVATE;

public class SensorService {
    private static final String TAG = "sensorService";

    private Context mContext;
    private SensorManager mSensorManager;
    private FusedLocationProviderClient fusedLocationClient;
    private UserInfoDao userInfoDao;
    private SharedPreferences sp;
    private Editor editor;

    public SensorService(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
//        sp = mContext.getSharedPreferences("location", MODE_PRIVATE);
//        editor = sp.edit();
        userInfoDao = AppDataBase.getInstance(mContext).userInfoDao();
    }

    public void setLocationInfo() {
        Log.d(TAG, "try to get location and save in database");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(mContext.getMainExecutor(), new OnSuccessListener<Location>() {
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
                            UserInfo userInfo = new UserInfo();
                            userInfo.setLatitude(latitude);
                            userInfo.setLongitude(longitude);
                            addUserInfo(userInfo);

//                            editor.putFloat("lat", latitude);
//                            editor.putFloat("lon", longitude);
//                            editor.apply();
                        } else {
                            Log.e(TAG, "cannot get location info");
                        }
                    }
                });
    }

    public String getWifiSSID() {
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;

        return wifiId;
    }

    public void addUserInfo(UserInfo newUserInfo) {
        UserInfo userInfo = userInfoDao.getUserInfo();
        if (userInfo != null) {
            newUserInfo.setId(userInfo.getId());
            userInfoDao.updateUserInfo(newUserInfo);
        }else {
            userInfoDao.addUserInfo(newUserInfo);
        }
    }

    public UserInfo getUserInfo() {
        return userInfoDao.getUserInfo();
    }
}
