package com.han.walktriggers.entity;

import android.app.PendingIntent;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationInfo {
    private String title;
    private String message;
    private Boolean isBigText;
    private Integer largeIconId;
    private Boolean hasLargeIcon = false;
    private Integer progress;
    private Boolean hasProgress = false;
    private Boolean hasAction = false;
    private PendingIntent pendingIntent;
    private Goal goal;

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

    public void setProgress(int progress) {
        this.progress = progress;
        this.hasProgress = true;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
        this.hasAction = true;
    }
}
