package com.han.walktriggers.data.online;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class WeatherService {
    private final static String TAG = "weatherService";
    private Context mContext;

    public WeatherService(Context context) {
        mContext = context;
    }

    public void getWeatherJson(double lat, double lon) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String weatherApi = "http://api.openweathermap.org/data/2.5/weather?";
        String apiId = "2e8962a7c5a75b5dc185b1811c573002";
        weatherApi = weatherApi + "&lat=" + lat + "&lon=" + lon + "&appid=" + apiId;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherApi,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //todo
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //todo
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void jsonHandle(String jsonStr) {

    }
}
