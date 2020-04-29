package com.roninaks.enqueue.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.dao.UserDao;
import com.roninaks.enqueue.databases.EnqueueDB;
import com.roninaks.enqueue.helpers.EmailHelper;
import com.roninaks.enqueue.helpers.SqlHelper;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.interfaces.SqlDelegate;
import com.roninaks.enqueue.models.UserModel;

import org.json.JSONObject;

public class UserRepository {
    private UserDao userDao;
    private Context context;

    public UserRepository(Application application){
        context = application.getApplicationContext();
        EnqueueDB enqueueDB = EnqueueDB.getInstance(application);
        userDao = enqueueDB.userDao();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public Context getContext() {
        return context;
    }

    public void insert(final UserModel userModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.insert(userModel);
            }
        }).start();
    }

    public void update(final UserModel userModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.update(userModel);
            }
        }).start();
    }

    public void delete(final UserModel userModel){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.delete(userModel);
            }
        }).start();
    }

    public void deleteAll(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.deleteAll();
            }
        }).start();
    }

    public LiveData<UserModel> getUser(int userId){
        return userDao.getUser(userId);
    }

    public LiveData<UserModel> getUser(String email){
        refreshData(email);
        return userDao.getUser(email);
    }

    public int hasUser(String email){
        return userDao.hasUser(email);
    }

    private void refreshData(final String email){
        SqlHelper sqlHelper = new SqlHelper(context, new SqlDelegate() {
            @Override
            public void onResponse(SqlHelper sqlHelper) {
                try {
                    JSONObject jsonObject = new JSONObject(sqlHelper.getStringResponse());
                    String response = jsonObject.getString("success");
                    if(response.equals(context.getString(R.string.success))){
                        final UserModel userModel = new UserModel(jsonObject.getJSONObject("data"));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(userDao.hasUser(email) <= 0)
                                    userDao.insert(userModel);
                                else
                                    userDao.update(userModel);
                            }
                        }).start();
                    }else{
                        Toast.makeText(context, context.getString(R.string.invalid_cred), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: UserRepository - getUser", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                    emailHelper.sendEmail();
                }
            }
        });
        sqlHelper.setExecutePath("login.php");
        ContentValues params = new ContentValues();
        params.put("username", email);
        sqlHelper.setParams(params);
        sqlHelper.setMethod(context.getString(R.string.method_post));
        sqlHelper.setService(true);
        sqlHelper.executeUrl(true);
    }
}
