package com.han.walktriggers.callback;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import com.han.walktriggers.TaskService;
import com.han.walktriggers.data.source.SensorService;
import com.han.walktriggers.entity.TriggerInfo;
import com.han.walktriggers.trigger.TriggerService;

import org.jetbrains.annotations.NotNull;


public class NetStateCallback extends ConnectivityManager.NetworkCallback {

    private static final String TAG = "NetStateCallback";
    private Context mContext;
    private TriggerService triggerService;

    public NetStateCallback(Context context) {
        this.mContext = context;
        triggerService = new TriggerService(mContext);
    }

    @Override
    public void onAvailable(@NotNull Network network) {
        super.onAvailable(network);
//        Log.d(TAG, "network is available");
    }

    @Override
    public void onCapabilitiesChanged(@NotNull Network network,
                                      @NotNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        // check whether the network is usable
        if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                String SSID = SensorService.getWifiSSID(mContext);
                TriggerInfo triggerInfo = new TriggerInfo();
                triggerInfo.setTaskName(TaskService.ACTION_CHECK_WIFI_SSID);
                triggerInfo.setStrParam1(SSID);
                triggerService.addTrigger(triggerInfo);

                Log.d(TAG, "network type is: wifi");
            } else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.d(TAG, "network type is: cellular");
            } else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                Log.d(TAG, "network type is: bluetooth");
            } else {
                Log.d(TAG, "other network type.");
            }
        }
    }
}
