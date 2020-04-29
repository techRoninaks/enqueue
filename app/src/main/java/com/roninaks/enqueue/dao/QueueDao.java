package com.roninaks.enqueue.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.roninaks.enqueue.models.QueueModel;

import java.util.List;

@Dao
public interface QueueDao {
    /***
     * Inserts QueueModel Object
     * @param queueModel - QueueModel Object to Insert
     */
    @Insert
    void insert(QueueModel queueModel);

    /***
     * Updates QueueModel Object
     * @param queueModel - QueueModel Object to Update
     */
    @Update
    void update(QueueModel queueModel);

    /***
     * Deletes QueueModel Object
     * @param queueModel - QueueModel Object to Delete
     */
    @Delete
    void delete(QueueModel queueModel);

    /***
     * Deletes all QueueModel Objects
     */
    @Query("DELETE FROM QueueModel")
    void deleteAll();

    /***
     * Retrieve all QueueModel Objects
     * @return Observable QueueModel List
     */
    @Query("SELECT * FROM QueueModel")
    LiveData<List<QueueModel>> getAllQueues();

    /***
     * Retrieve QueueModels objects of a particular service
     * @param serviceId - Service ID
     * @return Observable QueueModel list
     */
    @Query("SELECT * FROM QueueModel where ServiceId = :serviceId")
    LiveData<List<QueueModel>> getServiceQueues(int serviceId);

    /***
     * Retrieves a specific QueueModel object
     * @param queueId - Queue Id
     * @return Observable QueueModel object
     */
    @Query("SELECT * FROM QueueModel where QueueId = :queueId")
    LiveData<QueueModel> getQueue(int queueId);

    /***
     * Check whether a QueueModel object exists in Room
     * @param queueId - Queue Id
     * @return - If exists returns 1 otherwise returns 0
     */
    @Query("SELECT COUNT(*) FROM QueueModel where QueueId = :queueId")
    int hasQueue(int queueId);

    /***
     * Filters the QueueModel objects based on search keyword
     * @param searchKey - String search key
     * @return - Observable QueueModel list
     */
    @Query("SELECT * FROM QueueModel where (UserName like :searchKey or PhoneNumber like :searchKey) and ServiceId = :serviceId ")
    LiveData<List<QueueModel>> filterQueue(String searchKey, int serviceId);

    @Query("SELECT * FROM QueueModel ORDER BY QueueId DESC LIMIT 1")
    LiveData<QueueModel> getLatestToken();

    @Query("UPDATE QueueModel set QueueId = :newId where QueueId = :oldId")
    void updateId(int oldId, int newId);
}
