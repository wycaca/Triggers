package com.han.walktriggers;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.util.Log;

import com.han.walktriggers.data.source.ContextCustomer;
import com.han.walktriggers.entity.Goal;
import com.han.walktriggers.entity.History;
import com.han.walktriggers.entity.NotificationInfo;
import com.han.walktriggers.entity.UserInfo;
import com.han.walktriggers.data.source.WeatherService;
import com.han.walktriggers.entity.Weather;
import com.han.walktriggers.data.source.SensorService;
import com.han.walktriggers.callback.NetStateCallback;
import com.han.walktriggers.trigger.NotificationService;
import com.han.walktriggers.utils.DateUtils;
import com.han.walktriggers.utils.ProperUtil;

import java.util.List;

/**
 * This service includes different tasks, such as weather check.
 * same intent, but different tasks have different action names
 */
public class TaskService extends IntentService {
    // two or three weather api, compare and make sure
    public static final String ACTION_WEATHER = "com.han.walktriggers.action.WEATHER";
    public static final String ACTION_CHECK_PROGRESS = "com.han.walktriggers.action.CHECK_PROGRESS";
    // wifi SSID check, at home -> push step number and progress
    public static final String ACTION_CHECK_WIFI_SSID = "com.han.walktriggers.action.ACTION_CHECK_WIFI_SSID";
    // check history avg, less -> push
    public static final String ACTION_CHECK_HISTORY = "com.han.walktriggers.action.ACTION_CHECK_HISTORY";
    // user reached the goal in last 7 days, suggest them to change a easier one
    public static final String ACTION_SUGGEST_GOAL = "com.han.walktriggers.action.ACTION_SUGGEST_GOAL";

    public static final String ACTION_SET_GOAL = "com.han.walktriggers.action.ACTION_SET_GOAL";
    public static final String ACTION_PUSH_WEATHER = "com.han.walktriggers.action.ACTION_PUSH_WEATHER";

    public static final String EXTRA_PARAM1 = "com.han.walktriggers.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.han.walktriggers.extra.PARAM2";

    private static final String TAG = "TaskService";

    private SensorService sensorService;
    private WeatherService weatherService;
    private NotificationService notificationService;
    private ContextCustomer contextCustomer;


    public TaskService() {
        super("TaskService");
    }

    public static void startActionWeather(Context context) {
        Log.d(TAG, "startActionWeather");
        Intent intent = new Intent(context, TaskService.class);
        intent.setAction(ACTION_WEATHER);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sensorService = new SensorService(this);
        weatherService = new WeatherService(this);
        notificationService = new NotificationService(this);
        contextCustomer = new ContextCustomer(this);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WEATHER.equals(action)) {
                handleActionWeather();
            } else if (ACTION_CHECK_PROGRESS.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionCheckProgress();
            } else if (ACTION_CHECK_WIFI_SSID.equals(action)) {
                final String SSID = intent.getStringExtra(EXTRA_PARAM1);
                if (SSID == null) {
                    handleActionCheckWifiSSID();
                } else {
                    handleActionCheckWifiSSID(SSID);
                }
            } else if (ACTION_CHECK_HISTORY.equals(action)) {
                handleActionCheckHistory();
            } else if (ACTION_SUGGEST_GOAL.equals(action)) {
                handleActionSuggestGoal();
            } else if (ACTION_SET_GOAL.equals(action)) {
                final String name = intent.getStringExtra(EXTRA_PARAM1);
                final int num = intent.getIntExtra(EXTRA_PARAM2, 0);
                handleActionSetGoal(name, num);
            } else if (ACTION_PUSH_WEATHER.equals(action)) {
                final String main = intent.getStringExtra(EXTRA_PARAM1);
                pushWeatherNotification(main);
            }
        }
    }

    private void handleActionCheckProgress() {
        int target_process = 80;
        Log.d(TAG, "start handel check progress");
        NotificationInfo notificationInfo = new NotificationInfo();
        History history = contextCustomer.getLastHistory();
        int progress = history.getProgress();
        if (progress < target_process) {
            notificationInfo.setTitle("Goal is not finished!");
            // check whether today
            if (DateUtils.isToday(history.getTimestamp())) {
                notificationInfo.setMessage("Your goal progress is: " + progress
                        + ", less than " + target_process + ". Go!");
            } else {
                notificationInfo.setMessage("You haven't walked today. Go!");
            }
            notificationInfo.setProgress(progress);
            notificationService.pushNotification(notificationInfo);
        }
    }


    // call in main activity, only one time, register callback
    private void handleActionCheckWifiSSID() {
        Log.d(TAG, "register NetworkCallback");
        registerNetworkCallback();
    }

    // with param, check SSID when wifi is changed
    private void handleActionCheckWifiSSID(String SSID) {
        Log.d(TAG, "start handel check SSID");
        String homeSSID = ProperUtil.getProperties(this).getProperty("homeSSID");
        History history = contextCustomer.getLastHistory();

        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setTitle("Progress Check");
        if(SSID != null) {
            if (SSID.equals(homeSSID)) {
                notificationInfo.setMessage("You are at home now, check your step: "
                        + history.getStepNum() + "/" + history.getGoalNum());
                notificationInfo.setProgress(history.getProgress());
                notificationService.pushNotification(notificationInfo);
            }
            Log.d(TAG, "SSID: " + SSID);
        }
    }

    private void handleActionCheckHistory() {
        Log.d(TAG, "start handel check History");
        int maxDays = 7;
        int totalStep = 0;
        int todayStep = 0;
        int avgStep = 0;
        int dayNum;

        List<History> historyList = contextCustomer.getHistoryList();
        if (historyList.size() > 1) {
            if (historyList.size() > maxDays) {
                dayNum = maxDays;
            } else {
                dayNum = historyList.size();
            }
            int i = dayNum;
            while (i > 0) {
                for (History history : historyList) {
                    totalStep += history.getStepNum();
                    i--;
                }
            }
            avgStep = Math.round(totalStep / (float) dayNum);
            Log.d(TAG, "avg step: " + avgStep);
            if (DateUtils.isToday(historyList.get(0).getTimestamp())) {
                todayStep = historyList.get(0).getStepNum();
            }
            Log.d(TAG, "today step: " + todayStep);
            NotificationInfo notificationInfo = new NotificationInfo();
            notificationInfo.setTitle("History average step check");
            if (todayStep < avgStep) {
                notificationInfo.setMessage("Today, your step is: " + todayStep
                        + " less than last " + maxDays
                        +  " days' average steps: " + avgStep + ", GO!");
            } else {
                notificationInfo.setMessage("Today, your step is: " + todayStep
                        + " more than last " + maxDays
                        +  " days' average steps: " + avgStep + ", Keep it!");
            }
            notificationService.pushNotification(notificationInfo);
        }
    }

    private void handleActionSuggestGoal() {
        Log.d(TAG, "start handel check History");
        int maxDays = 7;
        int totalStep = 0;
        int avgStep = 0;
        boolean isReach = true;
        boolean isSmaller;

        List<History> historyList = contextCustomer.getHistoryList();
        List<Goal> goalList = contextCustomer.getGoalList();

        if (historyList != null && historyList.size() >= maxDays &&
                goalList != null && goalList.size() > 1) {
            int i = maxDays;
            while (i > 0) {
                for (History history : historyList) {
                    if (history.getProgress() < 100) {
                        totalStep += history.getStepNum();
                        isReach = false;
                    }
                    i--;
                }
            }
            avgStep = Math.round(totalStep / (float) maxDays);
            Log.d(TAG, "Is reach: " + isReach);
            Log.d(TAG, "avg step: " + avgStep);
            NotificationInfo notificationInfo = new NotificationInfo();
            notificationInfo.setTitle("History average step check");
            if (isReach) {
                isSmaller = false;
                notificationInfo.setMessage("Your have reached the last " + maxDays
                        +  " days' goals, Maybe you can challenge a more goal!");
            } else {
                isSmaller = true;
                notificationInfo.setMessage("Your haven't reached the last " + maxDays
                        +  " days' goals, Maybe you can select a less goal.");
            }
            Goal goal = getSuitableGoal(goalList, historyList.get(0).getGoalNum(), isSmaller);
            if (goal == null) {
                Log.d(TAG, "can't find a exist goal, use the avg step");
                goal = new Goal();
                goal.setGoalName(historyList.get(0).getGoalName());
                goal.setGoalNum(avgStep);
            }

            Intent intent = new Intent(this, TaskService.class);
            intent.setAction(ACTION_SET_GOAL);
            intent.putExtra(EXTRA_PARAM1, goal.getGoalName());
            intent.putExtra(EXTRA_PARAM2, goal.getGoalNum());
            PendingIntent operation = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationInfo.setPendingIntent(operation);
            notificationInfo.setGoal(goal);

            notificationService.pushNotification(notificationInfo);
        }
    }

    private Goal getSuitableGoal(List<Goal> goalList, int goalNum, Boolean isSmaller) {
        Log.d(TAG, "now goal num: " + goalNum);
        for (Goal g : goalList) {
                if (goalNum < g.getGoalNum()) {
                    if (!isSmaller) {
                        Log.d(TAG, "new goal: " + g.toString());
                        return g;
                    }
                } else if (goalNum > g.getGoalNum()){
                    if (isSmaller) {
                        Log.d(TAG, "new goal: " + g.toString());
                        return g;
                    }
                }
            }
        return null;
    }

    private void handleActionSetGoal(String goalName, int goalNum) {
        History history = contextCustomer.getLastHistory();
        history.setGoalName(goalName);
        history.setGoalNum(goalNum);
        contextCustomer.updateHistory(history);
    }

    private void registerNetworkCallback() {
        NetStateCallback netStateCallback = new NetStateCallback(this);
        NetworkRequest request = new NetworkRequest.Builder().build();
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            connectivityManager.registerNetworkCallback(request, netStateCallback);
        }
    }

    private void handleActionWeather() {
        Log.d(TAG, "start handel weather action");

        // get location
        SensorService sensorService = new SensorService(this);
        // get location info and save
        sensorService.setLocationInfo();

        UserInfo userInfo = sensorService.getUserInfo();
        if (userInfo != null && userInfo.getLatitude() != null) {
            Log.d(TAG, "check pass, do weather action");
            // send weather request
            weatherService.addWeatherRequest(userInfo.getLatitude(), userInfo.getLongitude(), 1);
            weatherService.addWeatherRequest(userInfo.getLatitude(), userInfo.getLongitude(), 2);
//            pushWeatherNotification();
        }
    }

    // same weather -> push
    private void pushWeatherNotification(String main) {
        NotificationInfo notificationInfo = new NotificationInfo();

        String title = "Today Weather";
        int iconId = 0;

        // get weather info
        Weather weather = weatherService.getNewestWeather();
        Log.d(TAG, "API1: " + weather.getCityName() + "  " + weather.getMain());
        Log.d(TAG, "API2: " + main);

        // check two weather information from different source is same
        if (weather.getMain().toLowerCase().contains(main)
                || (weather.getMain().equals("Clear") & (main.contains("sunny") || main.contains("clear") || main.contains("night")))) {
            Log.d(TAG, "weather information from two API is same, push to user");
            // notification
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(weather.toString());
            switch (weather.getMain()) {
                case "Rain":
                    iconId = R.drawable.ic_rain;
                    stringBuilder.append("You'd better take an umbrella. ");
                    break;
                case "Snow":
                    iconId = R.drawable.ic_snow;
                    stringBuilder.append("You'd better stay at home. ");
                    break;
                case "Clouds":
                    iconId = R.drawable.ic_cloud;
                    break;
                default:
                    iconId = R.drawable.ic_sunny;
                    stringBuilder.append("Have a nice day. ");
            }
            if (weather.getTemp() > 30) {
                stringBuilder.append("Today is very hot.");
            } else if (weather.getTemp() < 5) {
                stringBuilder.append("Today is very clod.");
            } else {
                stringBuilder.append("Keep health.");
            }

            notificationInfo.setTitle(title);
            notificationInfo.setMessage(stringBuilder.toString());
            notificationInfo.setLargeIconId(iconId);

            notificationService.pushNotification(notificationInfo);
        } else {
            Log.d(TAG, "weather information from two API is not same, stop push to user");
        }
    }
}
