package com.roninaks.enqueue.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.roninaks.enqueue.dao.QueueDao;
import com.roninaks.enqueue.dao.ServicePrimaryDao;
import com.roninaks.enqueue.dao.UserDao;
import com.roninaks.enqueue.models.QueueModel;
import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.models.UserModel;

@Database(entities = {ServicePrimaryModel.class, UserModel.class, QueueModel.class}, version = 1)
public abstract class EnqueueDB extends RoomDatabase {
    private static EnqueueDB instance;

    public abstract ServicePrimaryDao servicePrimaryDao();
    public abstract UserDao userDao();
    public abstract QueueDao queueDao();

    public static synchronized EnqueueDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    EnqueueDB.class, "enqueue_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
