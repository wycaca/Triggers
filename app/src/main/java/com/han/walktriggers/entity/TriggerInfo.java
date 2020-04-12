package com.han.walktriggers.entity;

import com.han.walktriggers.utils.DateUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TriggerInfo {
    private String taskName;
    // alarm will do on time
    private Boolean isAlarm;
    private Long time;

    private Integer type;

//    private NotificationInfo notificationInfo;

    public void setTime(int hour) {
        this.time = DateUtils.getHourToday(hour);
        isAlarm = true;
    }

    public void setTime(long time) {
        this.time = time;
        isAlarm = true;
    }
}
