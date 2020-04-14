package com.han.walktriggers.data.source;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.han.walktriggers.data.converters.Converters;
import com.han.walktriggers.entity.Goal;
import com.han.walktriggers.entity.History;
import com.han.walktriggers.utils.ProperUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ContextCustomer {

    private static final String TAG = "ContextCustomer";
    private String goalUrl;
    private String historyUri;
    private Context mContext;

    public ContextCustomer(Context context) {
        this.mContext = context;
        Properties properties = ProperUtil.getProperties(mContext);
        goalUrl = properties.getProperty("goalUri");
        historyUri = properties.getProperty("historyUri");
    }

    public List<Goal> getGoalList() {
        List<Goal> goalList = new ArrayList<>();
        Uri trackerUri = Uri.parse(goalUrl);
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(trackerUri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Goal goal = new Goal();
                int userId = cursor.getInt(0);
                String goalName = cursor.getString(1);
                int goalNum = cursor.getInt(2);

                goal.setUId(userId);
                goal.setGoalName(goalName);
                goal.setGoalNum(goalNum);

                goalList.add(goal);
                Log.d(TAG, goalName + " " + goalNum);
            }
        }
        cursor.close();
        return goalList;
    }

    public List<History> getHistoryList() {
        List<History> historyList = new ArrayList<>();
        Uri trackerUri = Uri.parse(historyUri);
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(trackerUri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                History history = new History();
                int hId = cursor.getInt(0);
                int uId = cursor.getInt(1);
                Long timestamp = cursor.getLong(2);
                int stepNum = cursor.getInt(3);
                String goalName = cursor.getString(4);
                int goalNum = cursor.getInt(5);
                int progress = cursor.getInt(6);

                history.setHId(hId);
                history.setUId(uId);
                history.setTimestamp(Converters.fromTimestamp(timestamp));
                history.setStepNum(stepNum);
                history.setGoalName(goalName);
                history.setGoalNum(goalNum);
                history.setProgress(progress);

                historyList.add(history);
                Log.d(TAG, history.toString());
            }
        }
        cursor.close();
        return historyList;
    }

    public History getLastHistory() {
        History history = new History();
        Uri trackerUri = Uri.parse(historyUri);
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(trackerUri, null, null, null, null);
        try {
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    int hId = cursor.getInt(cursor.getColumnIndex("h_id"));
                    int uId = cursor.getInt(1);
                    Date timestamp = Converters.fromTimestamp(cursor.getLong(2));
                    int stepNum = cursor.getInt(3);
                    String goalName = cursor.getString(4);
                    int goalNum = cursor.getInt(5);
                    int progress = cursor.getInt(6);

                    history.setHId(hId);
                    history.setUId(uId);
                    history.setTimestamp(timestamp);
                    history.setStepNum(stepNum);
                    history.setGoalName(goalName);
                    history.setGoalNum(goalNum);
                    history.setProgress(progress);

                    Log.d(TAG, history.toString());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return history;
    }

    public void updateHistory(History history) {
        ContentValues values = new ContentValues();
        Uri trackerUri = Uri.parse(historyUri);
        ContentResolver resolver = mContext.getContentResolver();
        String goalName = history.getGoalName();
        int goalNum = history.getGoalNum();
        int stepNum = history.getStepNum();
        int progress = Math.round((float) stepNum / (float) goalNum * 100);
        int hId = history.getHId();

        values.put("hId", hId);
        values.put("goalName", goalName);
        values.put("goalNum", goalNum);
        values.put("progress", progress);

        resolver.insert(trackerUri, values);
    }
}
