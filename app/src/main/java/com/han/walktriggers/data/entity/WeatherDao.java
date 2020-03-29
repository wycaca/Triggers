package com.han.walktriggers.data.entity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.han.walktriggers.data.entity.Weather;

@Dao
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addWeather(Weather weather);

    @Delete
    void delWeather(Weather weather);

    @Update
    void updateWeather(Weather ... weather);

    @Query("select * from weather order by timestamp desc limit 1")
    Weather getNewestWeather();
}
