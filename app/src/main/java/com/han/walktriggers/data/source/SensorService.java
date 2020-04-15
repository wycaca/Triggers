package com.han.walktriggers.data.source;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.han.walktriggers.data.AppDataBase;
import com.han.walktriggers.entity.UserInfo;
import com.han.walktriggers.data.dao.UserInfoDao;

import java.text.DecimalFormat;

public class SensorService {
    private static final String TAG = "sensorService";

    private Context mContext;
    private SensorManager mSensorManager;
    private FusedLocationProviderClient fusedLocationClient;
    private UserInfoDao userInfoDao;

    public SensorService(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        userInfoDao = AppDataBase.getInstance(mContext).userInfoDao();
    }

    public void setLocationInfo() {
        Log.d(TAG, "try to get location and save in database");
        // use google api to save power
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(mContext.getMainExecutor(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // save 0.00 and protect user privacy
                            DecimalFormat df = new DecimalFormat("#.00");
                            float latitude = Float.parseFloat(df.format(location.getLatitude()));
                            float longitude = Float.parseFloat(df.format(location.getLongitude()));

                            Log.d(TAG, "latitude: " + latitude + ", longitude: " + longitude);
                            UserInfo userInfo = new UserInfo();
                            userInfo.setLatitude(latitude);
                            userInfo.setLongitude(longitude);
                            addUserInfo(userInfo);
                        } else {
                            Log.e(TAG, "cannot get location info, so get it from database");
                        }
                    }
                });
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        assert wifiMgr != null;
        int wifiState = wifiMgr.getWifiState();
        if(wifiState == WifiManager.WIFI_STATE_ENABLED) {
            WifiInfo info = wifiMgr.getConnectionInfo();
            return info != null ? info.getSSID().replace("\"", "") : null;
        } else {
            Log.e(TAG, "wifi state is not correct: " + wifiState);
        }
        return null;
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
