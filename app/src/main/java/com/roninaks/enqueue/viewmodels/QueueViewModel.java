package com.roninaks.enqueue.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.roninaks.enqueue.models.QueueModel;
import com.roninaks.enqueue.repositories.QueueRepository;

import java.util.List;

public class QueueViewModel extends AndroidViewModel {
    private QueueRepository queueRepository;
    public QueueViewModel(@NonNull Application application) {
        super(application);
        queueRepository = new QueueRepository(application);
    }

    /***
     * Inserts QueueModel Object
     * @param queueModel - QueueModel Object to Insert
     */
    public void insert(QueueModel queueModel){
        queueRepository.insert(queueModel);
    }

    /***
     * Updates QueueModel Object
     * @param queueModel - QueueModel Object to Update
     */
    public void update(QueueModel queueModel){
        queueRepository.update(queueModel);
    }

    /***
     * Deletes QueueModel Object
     * @param queueModel - QueueModel Object to Delete
     */
    public void delete(QueueModel queueModel){
        queueRepository.delete(queueModel);
    }

    /***
     * Deletes all QueueModel Objects
     */
    public void deleteAll(){
        queueRepository.deleteAll();
    }

    /***
     * Retrieve all QueueModel Objects
     * @return Observable QueueModel List
     */
    public LiveData<List<QueueModel>> getAllQueues(){
        return queueRepository.getAllQueues();
    }

    /***
     * Retrieve QueueModels objects of a particular service
     * @param serviceId - Service ID
     * @return Observable QueueModel list
     */
    public LiveData<List<QueueModel>> getServiceQueues(int serviceId){
        return queueRepository.getServiceQueues(serviceId);
    }

    /***
     * Retrieves a specific QueueModel object
     * @param queueId - Queue Id
     * @return Observable QueueModel object
     */
    public LiveData<QueueModel> getQueue(int queueId){
        return queueRepository.getQueue(queueId);
    }

    /***
     * Filters the QueueModel objects based on search keyword
     * @param searchKey - String search key
     * @param serviceId - Service Id
     * @return - Observable QueueModel list
     */
    public LiveData<List<QueueModel>> filterQueue(String searchKey, int serviceId){
        return queueRepository.filterQueue(searchKey, serviceId);
    }

    /***
     * Get latest queue no
     * @return Observable QueueModel Object
     */
    public LiveData<QueueModel> getLatestToken(){
        return queueRepository.getLatestToken();
    }

    /***
     * Update ID
     * @param oldId - Original Id
     * @param newId - New Id
     */
    public void updateId(int oldId, int newId){
        queueRepository.updateId(oldId, newId);
    }
}
