package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.databinding.ActivityAlertDetailsBinding;

public class AlertDetails extends AppCompatActivity {

    ActivityAlertDetailsBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_alert_details);
        mBinding.executePendingBindings();
        Intent intent = getIntent();
        assignValues(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        assignValues(intent);
    }

    private void assignValues(Intent intent){
        if(intent.hasExtra("notification_id")){
            int id = intent.getIntExtra("notification_id", -1);
            mBinding.setId(id);
            String notificationType = "";
            switch (id){
                case NotificationsActivity.NOTIFICATION_PERSISTENT:{
                    notificationType = getString(R.string.notification_persistent);
                }
                break;
                case NotificationsActivity.NOTIFICATION_SCHEDULED:{
                    notificationType = getString(R.string.notification_scheduled);
                }
                break;
                case NotificationsActivity.NOTIFICATION_TRIGGERED:{
                    notificationType = getString(R.string.notification_trigered);
                }
                break;
                case NotificationsActivity.NOTIFICATION_ACTION:{
                    notificationType = getString(R.string.notification_action);
                }
                break;
                case NotificationsActivity.NOTIFICATION_EXPANDABLE:{
                    notificationType = getString(R.string.notification_expandable);
                }
                break;
                default:
                    notificationType = "Unknown";
            }
            mBinding.setType(notificationType);
        }
    }
}