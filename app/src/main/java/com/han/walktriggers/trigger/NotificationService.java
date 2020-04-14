package com.han.walktriggers.trigger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.han.walktriggers.R;
import com.han.walktriggers.entity.NotificationInfo;

public class NotificationService {

    private Context mContext;

    private static final String TAG = "NotificationService";

    private static final String CHANNEL_ID = "channel_id_1";
    private static final int NOTIFICATION_ID = 1;

    public NotificationService(Context context) {
        this.mContext = context;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void pushNotification(NotificationInfo notificationInfo) {
        createNotificationChannel();
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION_ID, createNotification(notificationInfo));
        }
    }

    private Notification createNotification(NotificationInfo notificationInfo) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(notificationInfo.getTitle())
                .setContentText(notificationInfo.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        if (notificationInfo.getIsBigText()) {
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(notificationInfo.getMessage()));
        }

        if (notificationInfo.getHasLargeIcon()) {
            Drawable vectorDrawable = mContext.getDrawable(notificationInfo.getLargeIconId());
            Bitmap icon = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(icon);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);

            builder.setLargeIcon(icon);
        }

        if (notificationInfo.getHasProgress()) {
            builder.setProgress(100, notificationInfo.getProgress(), false);
        }

        if (notificationInfo.getHasAction()) {
            builder.addAction(R.drawable.ic_notifications_active_24dp,
                    notificationInfo.getGoal().toString(), notificationInfo.getPendingIntent());
        }
        return builder.build();
    }
}
