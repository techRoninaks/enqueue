package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.roninaks.enqueue.EnqueueApplication;
import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends AppCompatActivity {
    //private constant string
    public static final int NOTIFICATION_PERSISTENT = 0;
    public static final int NOTIFICATION_SCHEDULED = 1;
    public static final int NOTIFICATION_TRIGGERED = 2;
    public static final int NOTIFICATION_ACTION = 3;
    public static final int NOTIFICATION_EXPANDABLE = 4;

    private boolean isNotificationScheduled = false;
    //Layout Binder
    private ActivityNotificationsBinding mBinding;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_persistent:{
                    generateNotification(NOTIFICATION_PERSISTENT);
                }
                break;
                case R.id.btn_scheduled:{
                    generateNotification(NOTIFICATION_SCHEDULED);
                }
                break;
                case R.id.btn_triggered:{
                    generateNotification(NOTIFICATION_TRIGGERED);
                }
                break;
                case R.id.btn_action:{
                    generateNotification(NOTIFICATION_ACTION);
                }
                break;
                case R.id.btn_expandable:{
                    generateNotification(NOTIFICATION_EXPANDABLE);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifications);
        mBinding.executePendingBindings();

        //Set Onclick listeners
        mBinding.btnPersistent.setOnClickListener(onClickListener);
        mBinding.btnScheduled.setOnClickListener(onClickListener);
        mBinding.btnTriggered.setOnClickListener(onClickListener);
        mBinding.btnAction.setOnClickListener(onClickListener);
        mBinding.btnExpandable.setOnClickListener(onClickListener);
    }

    private void generateNotification(int type){
        NotificationCompat.Builder builder = null;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, AlertDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("notification_id", type);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, type, intent, 0);
        switch (type){
            case NOTIFICATION_PERSISTENT:{
                builder = generatePersistentNotification();
            }
            break;
            case NOTIFICATION_ACTION:{

            }
            break;
            case NOTIFICATION_SCHEDULED:{
                if(isNotificationScheduled){
                    Toast.makeText(this, "Notification is already scheduled", Toast.LENGTH_SHORT).show();
                }else{
                    NotificationScheduler.buildNotificationScheduler(this).execute();
                    isNotificationScheduled = true;
                    Toast.makeText(this, "Notification has been scheduled", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case NOTIFICATION_TRIGGERED:{
                builder = generateTrigerredNotification();
            }
            break;
            case NOTIFICATION_EXPANDABLE:{
                builder = generateExpandableNotification();
            }
        }
        if (builder != null) {
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            notificationManager.notify(type, builder.build());
        }
    }

    /***
     * Generates a simple notification trigerred by a click
     * @return A simple notification
     */
    private NotificationCompat.Builder generateTrigerredNotification(){
        return new NotificationCompat.Builder(this, EnqueueApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(getString(R.string.notification_trigered))
                .setContentText(getString(R.string.notification_trigered).concat(" notification text"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /***
     * Generates
     * @return A notification that is expandable
     */
    private NotificationCompat.Builder generateExpandableNotification(){
        return new NotificationCompat.Builder(this, EnqueueApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(getString(R.string.notification_expandable))
                .setContentText(getString(R.string.notification_expandable).concat(" small text"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line. So we have created an expandable notification to make things simpler"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    /***
     * Generates a persistent notification
     * @return A simple notification that cannot be swiped away
     */
    private NotificationCompat.Builder generatePersistentNotification(){
        return new NotificationCompat.Builder(this, EnqueueApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(getString(R.string.notification_persistent))
                .setContentText(getString(R.string.notification_persistent).concat(" notification text"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);
    }

    /***
     * Generates a scheduled notification. Run from the AsyncTask
     */
    private void generateScheduledNotification(int id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, EnqueueApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(getString(R.string.notification_scheduled))
                .setContentText(getString(R.string.notification_scheduled).concat(" notification text" + System.currentTimeMillis()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(this, AlertDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("notification_id", NOTIFICATION_SCHEDULED);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_SCHEDULED, intent, 0);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
    }

    /***
     * Async task used to trigger scheduled notifications.
     */
    public static class NotificationScheduler extends AsyncTask<Void, Void, Void>{
        private static NotificationsActivity activity;
        @Override
        protected Void doInBackground(Void... voids) {
            int id = 100;
            while(id < 120){
                activity.generateScheduledNotification(id);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                id += 1;
            }
            return null;
        }

        private static NotificationScheduler buildNotificationScheduler(NotificationsActivity notificationsActivity){
            activity = notificationsActivity;
            return new NotificationScheduler();
        }
    }

}