package com.han.walktriggers.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationInfo {
    private String title;
    private String message;
    private Boolean isBigText;
    private Integer largeIconId;
    private Boolean hasLargeIcon;

    public void setMessage(String message) {
        this.message = message;
        this.isBigText = false;
        if (message.length() > 30) {
            isBigText = true;
        }
    }

    public void setLargeIconId(int iconId) {
        this.largeIconId = iconId;
        this.hasLargeIcon = true;
    }
}
