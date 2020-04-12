package com.han.walktriggers.utils;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

public class ProperUtil {
    public static final String TAG = "ProperUtil";

    public static Properties getProperties(Context mContext) {
        Properties props = new Properties();
        try {
            InputStream in = mContext.getAssets().open("dataSource");
            props.load(in);
        } catch (Exception e) {
            Log.e(TAG, "Read dataSource file failed. " + e.getMessage());
        }
        return props;
    }
}
