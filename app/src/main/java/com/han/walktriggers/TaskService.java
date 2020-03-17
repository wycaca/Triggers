package com.han.walktriggers;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.han.walktriggers.data.online.WeatherService;
import com.han.walktriggers.data.online.entity.Weather;
import com.han.walktriggers.data.sensor.SensorService;
import com.han.walktriggers.utils.DateUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class TaskService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_WEATHER = "com.han.walktriggers.action.WEATHER";
//    private static final String ACTION_BAZ = "com.han.walktriggers.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.han.walktriggers.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.han.walktriggers.extra.PARAM2";

    private static final String CHANNEL_ID = "channel_id_1";
    private static final String TAG = "TaskService";
    private static final int NOTIFICATION_ID = 1;

    private Context mContext;
    private SharedPreferences weatherSp;
    private SensorService sensorService;
    private WeatherService weatherService;


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
    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, TaskIntentService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mContext = getApplicationContext();
            final String action = intent.getAction();
            if (ACTION_WEATHER.equals(action)) {
                handleActionWeather();
            }
//            else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWeather() {
        Log.d(TAG, "start handel weather action");

        sensorService = new SensorService(mContext);
        weatherService = new WeatherService(mContext);
        weatherSp = mContext.getSharedPreferences("location", MODE_PRIVATE);

        // get location
        SensorService sensorService = new SensorService(this);
        // get location info and save in sp
        sensorService.setLocationSp();

        // send a weather request if success, save the weather info in database
        weatherService.addWeatherRequest(weatherSp.getFloat("lat", 0), weatherSp.getFloat("lon", 0));

        // get weather info
        Weather weather = weatherService.getNewestWeather();
        Log.d(TAG, weather.toString());
        // notification
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(weather.toString());
        switch (weather.getMain()) {
            case "Rain":
                stringBuilder.append("You'd better take an umbrella. ");
                break;
            case "Snow":
                stringBuilder.append("You'd better stay at home. ");
                break;
            default:
                stringBuilder.append("Have a nice day. ");
        }
        if(weather.getTemp() > 30) {
            stringBuilder.append("Today is very hot.");
        } else if (weather.getTemp() < 5) {
            stringBuilder.append("Today is very clod.");
        } else {
            stringBuilder.append("Keep health.");
        }
        String remindStr = stringBuilder.toString();
        pushWeatherNotification(remindStr);
    }

    private void pushWeatherNotification(String remindStr) {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Today Weather")
                .setContentText(remindStr)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(remindStr))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
