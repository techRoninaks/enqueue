package com.roninaks.enqueue;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class EnqueueApplication extends Application {
    public static final String NOTIFICATION_CHANNEL_ID = "default_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Default Channel";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "This is a default channel";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
