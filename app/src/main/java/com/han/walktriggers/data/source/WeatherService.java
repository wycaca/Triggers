package com.han.walktriggers.data.source;

import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.han.walktriggers.data.AppDataBase;
import com.han.walktriggers.data.entity.UserInfo;
import com.han.walktriggers.data.entity.Weather;
import com.han.walktriggers.data.entity.WeatherDao;
import com.han.walktriggers.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

public class WeatherService {
    private final static String TAG = "weatherService";
    private Context mContext;
    private WeatherDao weatherDao;
//    private SharedPreferences weatherSp;
    private SensorService sensorService;
    private AlarmManager mAlarmManager;
    private static int REQUEST_CODE = 1001;

    public WeatherService(Context context) {
        mContext = context;
        AppDataBase dataBase = AppDataBase.getInstance(mContext);
        weatherDao = dataBase.weatherDao();
//        weatherSp = mContext.getSharedPreferences("location", MODE_PRIVATE);
        sensorService = new SensorService(mContext);
    }

    public void addWeatherRequest(double lat, double lon) {
        // todo data source manage
        // more api -> improve
        // different -> don't call trigger

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String weatherApi = "https://api.openweathermap.org/data/2.5/weather?";
        String apiId = "2e8962a7c5a75b5dc185b1811c573002";
        weatherApi = weatherApi + "lat=" + lat + "&lon=" + lon + "&appid=" + apiId;

        Log.d(TAG, weatherApi);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jsonHandle(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void jsonHandle(String jsonStr) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonStr);
            String cityName = jsonObject.getString("name");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            String main = weatherObject.getString("main");
            String description = weatherObject.getString("description");
            JSONObject mainObj = jsonObject.getJSONObject("main");
            double temp = mainObj.getDouble("temp");
            double tempMax = mainObj.getDouble("temp_max");
            double tempMin = mainObj.getDouble("temp_min");
            Weather weather = new Weather();
            weather.setCityName(cityName);
            weather.setDescription(description);
            weather.setMain(main);
            weather.setTemp(tempTransform(temp));
            weather.setTempMax(tempTransform(tempMax));
            weather.setTempMin(tempTransform(tempMin));
            Log.d(TAG, weather.toString());
            // save in database
            insertWeather(weather);
        } catch (JSONException e) {
            Log.e(TAG, "weather json cannot be JsonObject, Pls check. /r" + e.getMessage());
        }
    }

    private Double tempTransform(Double temp) {
        if(temp != null) {
            // Kelvins to ℃
            temp = temp - 273.15;
            // keep 0.00
            BigDecimal bg = new BigDecimal(temp);
            temp = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return temp;
        } else {
            return null;
        }
    }

    private void insertWeather(Weather weather) {
        Weather oldWeather = weatherDao.getNewestWeather();
        Date now = new Date();
        if (oldWeather != null) {
            if (DateUtils.isSameDay(now, oldWeather.getTimeStamp())) {
                weather.setId(oldWeather.getId());
                updateWeather(weather);
            }
        } else {
            weatherDao.addWeather(weather);
        }
    }

    private void updateWeather(Weather weather) {
        weatherDao.updateWeather(weather);
    }

    public Weather getNewestWeather() {
        Weather weather = weatherDao.getNewestWeather();

        if(weather != null) {
            return weather;
        }else {
            UserInfo userInfo = sensorService.getUserInfo();
            if (userInfo != null && userInfo.getLatitude() != null) {
                addWeatherRequest(userInfo.getLatitude(), userInfo.getLongitude());
            } else {
                // when first time run, must no weather info
                addWeatherRequest(0,0);
            }

            weather = new Weather();
            return weather;
        }
    }
}
