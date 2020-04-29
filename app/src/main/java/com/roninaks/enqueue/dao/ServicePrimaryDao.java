package com.roninaks.enqueue.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.roninaks.enqueue.models.ServicePrimaryModel;

import java.util.List;

@Dao
public interface ServicePrimaryDao {
    /***
     * Inserts value in the ServicePrimary local entity
     * @param servicePrimaryModel Service Primary Model Object
     */
    @Insert
    void insert(ServicePrimaryModel servicePrimaryModel);

    /***
     * Updates value in the ServicePrimary local entity
     * @param servicePrimaryModel Service Primary Model Object
     */
    @Update
    void update(ServicePrimaryModel servicePrimaryModel);

    /***
     * Deletes value in the ServicePrimary local entity
     * @param servicePrimaryModel Service Primary Model Object
     */
    @Delete
    void delete(ServicePrimaryModel servicePrimaryModel);

    /***
     *Deletes all value in the ServicePrimary local entity
     */
    @Query("DELETE FROM ServicePrimaryModel")
    void deleteAll();

    /***
     *Selects all values in the ServicePrimary local entity
     */
    @Query("SELECT * FROM ServicePrimaryModel")
    LiveData<List<ServicePrimaryModel>> getAllServices();

    /***
     * Retrieves the service with specified service id
     * @param serviceId - ID of service to be fetched
     * @return Observable ServicePrimaryModel object
     */
    @Query("SELECT * FROM ServicePrimaryModel where ServiceId = :serviceId")
    LiveData<ServicePrimaryModel> getService(int serviceId);

    /***
     * Checks whether db contains any service
     * @return Returns 0 if does not exist, otherwise returns a positive integer
     */
    @Query("SELECT COUNT(*) FROM ServicePrimaryModel")
    int hasService();

    /***
     * Checks whether db contains the specified service
     * @return Returns 0 if does not exist, otherwise returns a positive integer
     */
    @Query("SELECT COUNT(*) FROM ServicePrimaryModel where ServiceId = :serviceId")
    int hasService(int serviceId);

    /***
     * Returns a filtered subset of ServicePrimaryModel objects
     * @param query - The string to filter
     * @return Filtered subset of Observable ServicePrimaryModel objects
     */
    @Query("SELECT * FROM ServicePrimaryModel where ServiceName LIKE :query")
    LiveData<List<ServicePrimaryModel>> filterServices(String query);

    @Query("UPDATE ServicePrimaryModel set ServiceId = :newId where ServiceId = :oldId")
    void updateId(int oldId, int newId);
}