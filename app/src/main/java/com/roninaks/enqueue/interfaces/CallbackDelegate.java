package com.roninaks.enqueue.interfaces;

import android.os.Bundle;

import java.util.HashMap;

public interface CallbackDelegate {
    void onResultReceived(String type, boolean resultCode, HashMap<String, String> extras);
    void onResultReceived(String type, boolean resultCode, Bundle extras);
}