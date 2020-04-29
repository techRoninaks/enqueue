package com.roninaks.enqueue.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.dao.ServicePrimaryDao;
import com.roninaks.enqueue.databases.EnqueueDB;
import com.roninaks.enqueue.helpers.EmailHelper;
import com.roninaks.enqueue.helpers.SqlHelper;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.interfaces.SqlDelegate;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.models.UserModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ServicePrimaryRepository {
    private static final String UPDATE_TYPE_UPDATE = "update";
    private static final String UPDATE_TYPE_INSERT = "insert";
    private static final String UPDATE_TYPE_DELETE = "delete";
    private static final String UPDATE_TYPE_DELETE_ALL = "delete_all";
    private ServicePrimaryDao servicePrimaryDao;
    private Context context;

    public ServicePrimaryRepository(Application application) {
        context = application.getApplicationContext();
        EnqueueDB enqueueDB = EnqueueDB.getInstance(application);
        servicePrimaryDao = enqueueDB.servicePrimaryDao();
        SharedPreferences sharedPreferences = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
        if (sharedPreferences.getBoolean(StringHelper.SHARED_PREFRENCE_SERVICE_REFRESH, true) || isTimeOut(sharedPreferences.getLong(StringHelper.SHARED_PREFRENCE_SERVICE_REFRESH_TIMEOUT, 0)))
            refreshAll();
    }

    public void insert(final ServicePrimaryModel servicePrimaryModel) {
        updateServer(servicePrimaryModel, UPDATE_TYPE_INSERT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                servicePrimaryDao.insert(servicePrimaryModel);
            }
        }).start();
    }

    public void update(final ServicePrimaryModel servicePrimaryModel) {
        updateServer(servicePrimaryModel, UPDATE_TYPE_UPDATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                servicePrimaryDao.update(servicePrimaryModel);
            }
        }).start();
    }

    public void delete(final ServicePrimaryModel servicePrimaryModel) {
        updateServer(servicePrimaryModel, UPDATE_TYPE_DELETE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                servicePrimaryDao.delete(servicePrimaryModel);
            }
        }).start();
    }

    public void deleteAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                servicePrimaryDao.deleteAll();
            }
        }).start();
    }

    public LiveData<List<ServicePrimaryModel>> getServicePrimaryModels() {
        return servicePrimaryDao.getAllServices();
    }

    public LiveData<ServicePrimaryModel> getService(int serviceId) {
        return servicePrimaryDao.getService(serviceId);
    }

    public LiveData<List<ServicePrimaryModel>> filterServices(String query) {
        return servicePrimaryDao.filterServices(query);
    }

    public void updateId(int oldId, int newId){
        servicePrimaryDao.updateId(oldId, newId);
    }

    private void refreshAll() {
        int userId = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0).getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1);
        if (userId >= 0) {
            SqlHelper sqlHelper = new SqlHelper(context, new SqlDelegate() {
                @Override
                public void onResponse(SqlHelper sqlHelper) {
                    try {
                        JSONObject jsonObject = new JSONObject(sqlHelper.getStringResponse());
                        String response = jsonObject.getString("success");
                        if (response.equals(context.getString(R.string.success))) {
                            final JSONArray jsonArray = jsonObject.getJSONArray("data");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            JSONObject jobj = jsonArray.getJSONObject(i);
                                            final ServicePrimaryModel servicePrimaryModel = new ServicePrimaryModel(jobj);
                                            if (servicePrimaryDao.hasService(servicePrimaryModel.getServiceId()) <= 0)
                                                servicePrimaryDao.insert(servicePrimaryModel);
                                            else
                                                servicePrimaryDao.update(servicePrimaryModel);
                                            SharedPreferences sharedPreferences = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0);
                                            sharedPreferences.edit().putLong(StringHelper.SHARED_PREFRENCE_SERVICE_REFRESH_TIMEOUT, System.currentTimeMillis()).apply();
                                            sharedPreferences.edit().putBoolean(StringHelper.SHARED_PREFRENCE_SERVICE_REFRESH, false).apply();
                                        }catch (Exception e){

                                        }
                                    }
                                }
                            }).start();
                        } else {
                            Toast.makeText(context, context.getString(R.string.invalid_cred), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ServicesPrimaryRepository - refreshAll", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                        emailHelper.sendEmail();
                    }
                }
            });
            sqlHelper.setExecutePath("get-service-details.php");
            ContentValues params = new ContentValues();
            params.put("user_id", userId);
            sqlHelper.setParams(params);
            sqlHelper.setMethod(context.getString(R.string.method_get));
            sqlHelper.setService(true);
            sqlHelper.executeUrl(true);
        }
    }

    private boolean isTimeOut(long millis) {
        boolean reset = false;
        final long timeout = 24 * 60 * 60 * 100;
        if (System.currentTimeMillis() - millis > timeout)
            reset = true;
        return reset;
    }

    private void updateServer(ServicePrimaryModel servicePrimaryModel, final String updateType){
        try {
            SqlHelper sqlHelper = new SqlHelper(context, new SqlDelegate() {
                @Override
                public void onResponse(final SqlHelper sqlHelper) {
                    try {
                        if (updateType.equals(UPDATE_TYPE_INSERT)) {
                            final JSONObject jsonReponse = new JSONObject(sqlHelper.getStringResponse());
                            String response = jsonReponse.getString("success");
                            if (response.equals(context.getString(R.string.success))) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
//                                            queueModel.setQueueId(jsonReponse.getInt("new_id"));
//                                            queueDao.update(queueModel);
                                            servicePrimaryDao.updateId(jsonReponse.getInt("old_id"), jsonReponse.getInt("new_id"));
                                        } catch (Exception e) {
                                            Log.d("Error", "Error in onResponse");
                                        }
                                    }
                                }).start();
                            }
                        }
                    } catch (Exception e) {
                        EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ServicesPrimaryRepository - updateServer", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
                        emailHelper.sendEmail();
                    }
                }
            });
            sqlHelper.setExecutePath("update-services.php");
            ContentValues params = new ContentValues();
            if (servicePrimaryModel != null) {
                JSONObject jsonObject = servicePrimaryModel.exportToJson();
                params.put("values", jsonObject.toString());
            }
            params.put("update_type", updateType);
            sqlHelper.setParams(params);
            sqlHelper.setMethod(context.getString(R.string.method_post));
            sqlHelper.setService(true);
            sqlHelper.executeUrl(false);
        } catch (Exception e) {
            EmailHelper emailHelper = new EmailHelper(context, EmailHelper.TECH_SUPPORT, "Error: ServicesPrimaryRepository - updateServer", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
