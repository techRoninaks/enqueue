package com.roninaks.enqueue.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.helpers.SqlHelper;

public class NoInternetActivity extends AppCompatActivity {

    public static SqlHelper sqlHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
    }
}
