package com.roninaks.enqueue.models;

import android.os.SystemClock;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.roninaks.enqueue.entityx.QueueModelx;
import com.roninaks.enqueue.helpers.EntityxBridge;

import org.json.JSONObject;

import java.lang.reflect.Field;

@Entity
public class QueueModel extends EntityxBridge {

    @PrimaryKey(autoGenerate = true)
    private int QueueId;
    private int ServiceId;
    private String UserName;
    private String PhoneNumber;
    private long ServicedTime;
    private String Status;
    private int Token;
    private String DateOfService;
    private long StartTime;

    public QueueModel() {
        //Empty constructor
    }
    public QueueModel(JSONObject jsonObject) throws Exception{
        importFromJson(jsonObject);
    }

    public QueueModel(int queueId, int serviceId, String userName, String phoneNumber, int servicedTime, String status, int token, String dateOfService) {
        QueueId = queueId;
        ServiceId = serviceId;
        UserName = userName;
        PhoneNumber = phoneNumber;
        ServicedTime = servicedTime;
        Status = status;
        Token = token;
        DateOfService = dateOfService;
    }

    //User name
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    //Phone number
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    //Serviced Time
    public long getServicedTime() {
        return ServicedTime;
    }

    public void setServicedTime(long servicedTime) {
        ServicedTime = servicedTime;
    }

    //Status
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
        if(status != null && !status.isEmpty() && status.equals("ongoing"))
            setStartTime(SystemClock.elapsedRealtime());
    }

    //Token
    public int getToken() {
        return Token;
    }

    public void setToken(int token) {
        Token = token;
    }

    //Queue ID
    public int getQueueId() {
        return QueueId;
    }

    public void setQueueId(int queueId) {
        QueueId = queueId;
    }

    //Service ID
    public int getServiceId() {
        return ServiceId;
    }

    public void setServiceId(int serviceId) {
        ServiceId = serviceId;
    }

    //Start Time
    public long getStartTime() {
        return StartTime;
    }

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    //Date of Service
    public String getDateOfService() {
        return DateOfService;
    }

    public void setDateOfService(String dateOfService) {
        DateOfService = dateOfService;
    }

    @Override
    protected void importFromJson(JSONObject jsonObject) throws Exception {
        QueueModelx queueModelx = new QueueModelx();
        queueModelx.importFromJson(jsonObject);
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Field fieldx = queueModelx.getClass().getDeclaredField(field.getName());
                field.set(this, fieldx.get(queueModelx));
            }catch (NoSuchFieldException e){
                //Ignore the field
            }
        }
    }

    @Override
    public JSONObject exportToJson() throws Exception {
        QueueModelx queueModelx = new QueueModelx();
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Field fieldx = queueModelx.getClass().getDeclaredField(field.getName());
                fieldx.set(queueModelx, field.get(this));
            }catch (NoSuchFieldException e){
                //Ignore the field
            }
        }
        return queueModelx.exportToJson();
    }

}
