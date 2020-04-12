package com.han.walktriggers.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import lombok.Data;

@Data
@Entity(indices = @Index(value = {"w_id"}, unique = true), tableName = "weather")
public class Weather {

    public Weather() {
        timeStamp = new Date();
    }

    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "w_id")
    private Integer id;

    @ColumnInfo(name = "city_name")
    private String cityName;

    @ColumnInfo(name = "temp")
    private Double temp;

    @ColumnInfo(name = "temp_max")
    private Double tempMax;

    @ColumnInfo(name = "temp_min")
    private Double tempMin;

    @ColumnInfo(name = "main")
    private String main;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "timestamp")
    private Date timeStamp;

    public String toString() {
        return description + ", Temperature: " + temp + ", max: " + tempMax + ", min: " + tempMin + ". ";
    }
}
