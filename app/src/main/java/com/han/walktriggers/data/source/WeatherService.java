package com.han.walktriggers.data.source;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.han.walktriggers.TaskService;
import com.han.walktriggers.data.AppDataBase;
import com.han.walktriggers.entity.UserInfo;
import com.han.walktriggers.entity.Weather;
import com.han.walktriggers.data.dao.WeatherDao;
import com.han.walktriggers.utils.DateUtils;
import com.han.walktriggers.utils.ProperUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

public class WeatherService {
    private final static String TAG = "weatherService";
    private Context mContext;
    private WeatherDao weatherDao;
    private SensorService sensorService;
    private Properties dataSourcePro;
    private static int REQUEST_CODE = 1001;

    public WeatherService(Context context) {
        mContext = context;
        AppDataBase dataBase = AppDataBase.getInstance(mContext);
        weatherDao = dataBase.weatherDao();
        sensorService = new SensorService(mContext);
        dataSourcePro = ProperUtil.getProperties(mContext);
    }

    public void addWeatherRequest(float lat, float lon, final int type) {
        // data source manage
        // more api -> improve
        // different -> don't call trigger

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);

        String weatherApi = "";
        if (type == 1) {
            weatherApi = weatherApi1(lat, lon);
        } else if (type == 2) {
            weatherApi = weatherApi2(lat, lon);
        }
        Log.d(TAG, weatherApi);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (type == 1) {
                            jsonHandle(response);
                        } else if (type == 2) {
                            String main = jsonHandle2(response);
                            if (main != null) {
                                Intent intent = new Intent(mContext, TaskService.class);
                                intent.setAction(TaskService.ACTION_PUSH_WEATHER);
                                intent.putExtra(TaskService.EXTRA_PARAM1, main);
                                mContext.startService(intent);
                            }
                        }
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

    private String weatherApi1(float lat, float lon) {
        String weatherApi = dataSourcePro.getProperty("weatherApi");
        String apiId = dataSourcePro.getProperty("apiId");
        return weatherApi + "lat=" + lat + "&lon=" + lon + "&appid=" + apiId;
    }

    private String weatherApi2(float lat, float lon) {
        String weatherApi = dataSourcePro.getProperty("weatherApi2");
        String apiId = dataSourcePro.getProperty("apiId2");
        weatherApi = weatherApi + apiId;
        return weatherApi + "&unit=m&query=" + lat + "," + lon;
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

    private String jsonHandle2(String jsonStr) {
        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(jsonStr);
            JSONObject location = jsonObject.getJSONObject("location");
            String cityName = location.getString("name");
            JSONObject current = jsonObject.getJSONObject("current");
            String iconUrl = current.getJSONArray("weather_icons").get(0).toString();
            String temp = iconUrl.split("_")[iconUrl.split("_").length - 1];
            String main = temp.split("\\.")[0];

//            Weather weather = new Weather();
//            weather.setCityName(cityName);
//            weather.setTemp(current.getDouble("temperature"));
//            weather.setMain(main);
//            weather.setDescription(current.getJSONArray("weather_descriptions").get(0).toString());
//            Log.d(TAG, weather.toString());

            return main;
        } catch (JSONException e) {
            Log.e(TAG, "weather json cannot be JsonObject, Pls check. /r" + e.getMessage());
        } catch (IndexOutOfBoundsException e){
            Log.e(TAG, "main text error, Pls check. /r" + e.getMessage());
        }
        return null;
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
        if (oldWeather != null) {
            if (DateUtils.isSameDay(new Date(), oldWeather.getTimeStamp())) {
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
                addWeatherRequest(userInfo.getLatitude(), userInfo.getLongitude(), 1);
            } else {
                // when first time run, must no weather info
                addWeatherRequest(0,0, 1);
            }
            weather = new Weather();
            return weather;
        }
    }
}
