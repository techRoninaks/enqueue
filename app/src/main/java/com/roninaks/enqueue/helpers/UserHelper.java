package com.roninaks.enqueue.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.Observer;

import com.roninaks.enqueue.activities.LoginActivity;
import com.roninaks.enqueue.models.UserModel;
import com.roninaks.enqueue.repositories.QueueRepository;
import com.roninaks.enqueue.repositories.ServicePrimaryRepository;
import com.roninaks.enqueue.repositories.UserRepository;

public class UserHelper {
    public static UserModel userModel;
    private Context context;

    public UserHelper(Context context) {
        this.context = context;
        if (userModel == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
            int userId = sharedPreferences.getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1);
            UserRepository userRepository = new UserRepository(((Activity) context).getApplication());
            userRepository.getUser(userId).observeForever(new Observer<UserModel>() {
                @Override
                public void onChanged(UserModel userModel) {
                    if (UserHelper.userModel != null && userModel != null && !UserHelper.userModel.getPassword().equals(userModel.getPassword())) {
                        logout();
                        return;
                    }
                    UserHelper.userModel = userModel;
                }
            });
        }
    }

    public void logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
        sharedPreferences.edit().putInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1).apply();
        sharedPreferences.edit().putBoolean(StringHelper.SHARED_PREFRENCE_SERVICE_REFRESH, true).apply();
        sharedPreferences.edit().putBoolean(StringHelper.SHARED_PREFERENCE_USER_RESET, true).apply();
        //Clear ServiceModels
        new ServicePrimaryRepository(((Activity) context).getApplication()).deleteAll();
        //Clear QueueModels
        new QueueRepository(((Activity) context).getApplication()).deleteAll();
        userModel = null;
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
