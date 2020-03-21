package com.han.walktriggers.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(indices = @Index(value = {"id"}, unique = true), tableName = "user_info")
public class UserInfo {
    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "latitude")
    Float latitude;
    @ColumnInfo(name = "longitude")
    Float longitude;

    @ColumnInfo(name = "home_wifi")
    String homeWIFI;
    @ColumnInfo(name = "work_wifi")
    String workWIFI;
}
