package com.roninaks.enqueue.repositories;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.roninaks.enqueue.R;
import com.roninaks.enqueue.dao.QueueDao;
import com.roninaks.enqueue.databases.EnqueueDB;
import com.roninaks.enqueue.helpers.EmailHelper;
import com.roninaks.enqueue.helpers.SqlHelper;
import com.roninaks.enqueue.helpers.StringHelper;
import com.roninaks.enqueue.interfaces.SqlDelegate;
import com.roninaks.enqueue.models.QueueModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class QueueRepository {
    private Context context;
    private QueueDao queueDao;
    private final static String UPDATE_TYPE_UPDATE = "update";
    private final static String UPDATE_TYPE_INSERT = "insert";
    private final static String UPDATE_TYPE_DELETE = "delete";
    private final static String UPDATE_TYPE_DELETE_ALL = "delete_all";

    public QueueRepository(Application application) {
        context = application.getApplicationContext();
        EnqueueDB enqueueDB = EnqueueDB.getInstance(application);
        queueDao = enqueueDB.queueDao();
    }

    public void insert(final QueueModel queueModel) {
        updateServer(queueModel, UPDATE_TYPE_INSERT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                queueDao.insert(queueModel);
            }
        }).start();
    }

    public void update(final QueueModel queueModel) {
        updateServer(queueModel, UPDATE_TYPE_UPDATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                queueDao.update(queueModel);
            }
        }).start();
    }

    public void delete(final QueueModel queueModel) {
        updateServer(queueModel, UPDATE_TYPE_DELETE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                queueDao.delete(queueModel);
            }
        }).start();
    }

    public void deleteAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                queueDao.deleteAll();
//                updateServer(null, UPDATE_TYPE_INSERT);
            }
        }).start();
    }

    public LiveData<List<QueueModel>> getAllQueues() {
        refreshQueue();
        return queueDao.getAllQueues();
    }

    public LiveData<List<QueueModel>> getServiceQueues(int serviceId) {
        refreshQueue();
        return queueDao.getServiceQueues(serviceId);
    }

    public LiveData<QueueModel> getQueue(int queueId) {
        refreshQueue();
        return queueDao.getQueue(queueId);
    }

    public LiveData<List<QueueModel>> filterQueue(String searchKey, int serviceId) {
        refreshQueue();
        return queueDao.filterQueue(searchKey, serviceId);
    }

    public LiveData<QueueModel> getLatestToken() {
        refreshQueue();
        return queueDao.getLatestToken();
    }

    public void updateId(final int oldId, final int newId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                queueDao.updateId(oldId, newId);
            }
        }).start();
    }

    private void refreshQueue() {
        int userId = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0).getInt(StringHelper.SHARED_PREFERENCE_USER_ID, -1);
        int queueId = context.getSharedPreferences(StringHelper.SHARED_PREFERENCE_KEY, 0).getInt(StringHelper.SHARED_PREFERENCE_LAST_QUEUE_ID, -1);
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
                                    try {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jobj = jsonArray.getJSONObject(i);
                                            final QueueModel queueModel = new QueueModel(jobj);
                                            if (queueDao.hasQueue(queueModel.getQueueId()) <= 0) {
                                                if(queueModel.getStatus().equals(context.getString(R.string.queue_status_ongoing)))
                                                    queueModel.setStartTime(SystemClock.elapsedRealtime());
                                                queueDao.insert(queueModel);
                                            }
                                            else {
                                                if(queueModel.getStatus().equals(context.getString(R.string.queue_status_ongoing))){
                                                    LiveData<QueueModel> tempObj = queueDao.getQueue(queueModel.getQueueId());
                                                    queueModel.setStartTime(tempObj.getValue().getStartTime());
                                                }
                                                queueDao.update(queueModel);
                                            }
                                        }
                                    } catch (Exception e) {

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
            sqlHelper.setExecutePath("get-queues.php");
            ContentValues params = new ContentValues();
            params.put("user_id", userId);
            params.put("queue_id", queueId);
            sqlHelper.setParams(params);
            sqlHelper.setMethod(context.getString(R.string.method_get));
            sqlHelper.setService(true);
            sqlHelper.executeUrl(true);
        }
    }

    private void updateServer(final QueueModel queueModel, final String updateType) {
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
                                            queueDao.updateId(jsonReponse.getInt("old_id"), jsonReponse.getInt("new_id"));
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
            sqlHelper.setExecutePath("update-queues.php");
            ContentValues params = new ContentValues();
            if (queueModel != null) {
                JSONObject jsonObject = queueModel.exportToJson();
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
