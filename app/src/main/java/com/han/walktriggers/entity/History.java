package com.han.walktriggers.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class History {
    @NotNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "h_id")
    private Integer hId;

    @ColumnInfo(name = "u_id")
    private Integer uId;

    @ColumnInfo(name = "timestamp")
    private Date timestamp;

    @ColumnInfo(name = "step_num")
    private Integer stepNum;

    @ColumnInfo(name = "goal_name")
    private String goalName;

    @ColumnInfo(name = "goal_num")
    private Integer goalNum;

    @ColumnInfo(name = "progress")
    private Integer progress;

    @Override
    public String toString() {
        return timestamp + ": " + stepNum + "/" + goalNum + "(" + goalName + "), " + progress + '%';
    }

    public static void main(String[] args) {
        History history = new History();
        history.setStepNum(100);
        history.setGoalName("goal1");
        history.setGoalNum(2000);
        history.setTimestamp(new Date());
        history.setProgress(Math.round((float) 100/ (float)2000 * 100));
        System.out.println(history.toString());
    }
}
