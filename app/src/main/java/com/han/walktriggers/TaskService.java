package com.han.walktriggers;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import android.util.Log;

import com.han.walktriggers.data.source.ContextCustomer;
import com.han.walktriggers.entity.History;
import com.han.walktriggers.entity.NotificationInfo;
import com.han.walktriggers.entity.UserInfo;
import com.han.walktriggers.data.source.WeatherService;
import com.han.walktriggers.entity.Weather;
import com.han.walktriggers.data.source.SensorService;
import com.han.walktriggers.trigger.notification.NotificationService;

/**
 * This service includes different tasks, such as weather check.
 * same intent, but different tasks have different action names
 */
public class TaskService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_WEATHER = "com.han.walktriggers.action.WEATHER";
    public static final String ACTION_CHECK_PROGRESS = "com.han.walktriggers.action.CHECK_PROGRESS";

//    private static final String EXTRA_PARAM1 = "com.han.walktriggers.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "com.han.walktriggers.extra.PARAM2";

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

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, TaskIntentService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

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
            }
            else if (ACTION_CHECK_PROGRESS.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionCheckProgress();
            }
        }
    }

    private void handleActionCheckProgress() {
        int target_process = 80;
        Log.d(TAG, "start handel check progress");
        NotificationInfo notificationInfo = new NotificationInfo();

        History history = contextCustomer.getLastHistory();
        int progress = history.getProgress();
        if (history != null) {
            if (progress < target_process) {
                notificationInfo.setTitle("Goal is not finished!");
                notificationInfo.setMessage("Your goal progress is: " + progress
                        + ", less than " + target_process + ". Go!");
                notificationInfo.setProgress(progress);

                notificationService.pushNotification(notificationInfo);
            }
        }

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWeather() {
        Log.d(TAG, "start handel weather action");

        // get location
        SensorService sensorService = new SensorService(this);
        // get location info and save
        sensorService.setLocationInfo();

        UserInfo userInfo = sensorService.getUserInfo();
        // send a weather request if success, save the weather info in database
//        weatherService.addWeatherRequest(weatherSp.getFloat("lat", 0), weatherSp.getFloat("lon", 0));
        if (userInfo != null && userInfo.getLatitude() != null) {
            Log.d(TAG, "check pass, do weather action");
            weatherService.addWeatherRequest(userInfo.getLatitude(), userInfo.getLongitude());
            pushWeatherNotification();
        }
    }

    // todo Notification manage
    // todo same weather -> different triggers
    private void pushWeatherNotification() {
        NotificationInfo notificationInfo = new NotificationInfo();

        String title = "Today Weather";
        int iconId = 0;

        // get weather info
        Weather weather = weatherService.getNewestWeather();
        Log.d(TAG, weather.getCityName());
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
        if(weather.getTemp() > 30) {
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
    }



    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
