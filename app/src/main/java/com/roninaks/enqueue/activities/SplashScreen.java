package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.helpers.EmailHelper;
import com.roninaks.enqueue.helpers.StringHelper;

public class SplashScreen extends AppCompatActivity {
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performChecks();
        setContentView(R.layout.activity_splash_screen);
    }

    /***
     * Perform required checks
     */
    private void performChecks(){
        //Check whether user is logged in
        if(isLoggedIn()){
            Intent intent = new Intent(this, ServicesActivity.class);
            intent.putExtra(ServicesActivity.INTENT_PARAM_USERID, userId);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }

    /***
     * Checks whether a user is logged in
     * @return True if a user is logged in. False if no users are logged in.
     */
    private boolean isLoggedIn(){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
            if ((userId = sharedPreferences.getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1)) >= 0) {
                return true;
            }
        } catch (Exception e) {
            EmailHelper emailHelper = new EmailHelper(SplashScreen.this, EmailHelper.TECH_SUPPORT, "Error: SplashScreen - isLoggedIn()", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return false;
    }
}
