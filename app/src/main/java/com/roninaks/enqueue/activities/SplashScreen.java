package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.roninaks.enqueue.R;

public class SplashScreen extends AppCompatActivity {

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
        isLoggedIn();
    }

    /***
     * Checks whether a user is logged in
     * @return True if a user is logged in. False if no users are logged in.
     */
    private boolean isLoggedIn(){
        return false;
    }
}
