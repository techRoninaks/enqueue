package com.roninaks.enqueue.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.roninaks.enqueue.entityx.ServicePrimaryModelx;
import com.roninaks.enqueue.helpers.EntityxBridge;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Entity
public class ServicePrimaryModel extends EntityxBridge {
    @PrimaryKey
    private int ServiceId;
    private int UserId;
    private String ServiceName;
    private int WaitingCount;
    private int AvgWaitingTime;
    private int Served;
    private String Description;
    private String Slug;
    private int QueueLimit;
    private int WaitLimit;
    private String Status;
//    private Date ServiceDate;
    @Ignore
    private ArrayList<QueueModel> QueueModels;

    public ServicePrimaryModel(){
        //Empty Constructor
    }

    public ServicePrimaryModel(JSONObject jsonObject) throws Exception{
        importFromJson(jsonObject);
    }

    //Date
//    public Date getServiceDate() {
//        return ServiceDate;
//    }
//    public void setServiceDate(Date serviceDate) {
//        ServiceDate = serviceDate;
//    }

    //Service Id
    public void setServiceId(int serviceId) {
        ServiceId = serviceId;
    }
    public int getServiceId() {
        return ServiceId;
    }

    //User Id
    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    //Service Name
    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    //Waiting Count
    public int getWaitingCount() {
        return WaitingCount;
    }

    public void setWaitingCount(int waitingCount) {
        WaitingCount = waitingCount;
    }

    //Average waiting time
    public int getAvgWaitingTime() {
        return AvgWaitingTime;
    }

    public void setAvgWaitingTime(int avgWaitingTime) {
        AvgWaitingTime = avgWaitingTime;
    }


    //Served Count
    public int getServed() {
        return Served;
    }

    public void setServed(int served) {
        Served = served;
    }

    //Description
    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    //Slug
    public String getSlug() {
        return Slug;
    }

    public void setSlug(String slug) {
        Slug = slug;
    }

    //Queue Limit
    public int getQueueLimit() {
        return QueueLimit;
    }

    public void setQueueLimit(int queueLimit) {
        QueueLimit = queueLimit;
    }

    //Wait limit
    public int getWaitLimit() {
        return WaitLimit;
    }

    public void setWaitLimit(int waitLimit) {
        WaitLimit = waitLimit;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    protected void importFromJson(JSONObject jsonObject) throws Exception {
        ServicePrimaryModelx servicePrimaryModelx = new ServicePrimaryModelx();
        servicePrimaryModelx.importFromJson(jsonObject);
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Field fieldx = servicePrimaryModelx.getClass().getDeclaredField(field.getName());
                field.set(this, fieldx.get(servicePrimaryModelx));
            }catch (NoSuchFieldException e){
                //Ignore the field
            }
        }
    }

    @Override
    public JSONObject exportToJson() throws Exception {
        ServicePrimaryModelx servicePrimaryModelx = new ServicePrimaryModelx();
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Field fieldx = servicePrimaryModelx.getClass().getDeclaredField(field.getName());
                fieldx.set(servicePrimaryModelx, field.get(this));
            }catch (NoSuchFieldException e){
                //Ignore the field
            }
        }
        return servicePrimaryModelx.exportToJson();
    }


}
