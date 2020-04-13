package com.han.walktriggers.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Goal {
//    @NotNull
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "g_id")
//    private Integer id;

    @ColumnInfo(name = "u_id")
    private Integer uId;

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "g_name")
    private String goalName;

    @ColumnInfo(name = "g_num")
    private Integer goalNum;

    @NotNull
    @Override
    public String toString() {
        return goalName + ' ' + goalNum;
    }
}
