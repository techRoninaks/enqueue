package com.roninaks.enqueue.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.roninaks.enqueue.models.ServicePrimaryModel;
import com.roninaks.enqueue.repositories.ServicePrimaryRepository;

import java.util.List;

public class ServicePrimaryViewModel extends AndroidViewModel {
    private ServicePrimaryRepository servicePrimaryRepository;

    public ServicePrimaryViewModel(@NonNull Application application) {
        super(application);
        servicePrimaryRepository = new ServicePrimaryRepository(application);
    }

    /***
     * Inserts a ServicePrimaryModel object into Room
     * @param servicePrimaryModel - The object to insert
     */
    public void insert(ServicePrimaryModel servicePrimaryModel){
        servicePrimaryRepository.insert(servicePrimaryModel);
    }

    /***
     * Update a ServicePrimaryModel object in Room
     * @param servicePrimaryModel - The object to update
     */
    public void update(ServicePrimaryModel servicePrimaryModel){
        servicePrimaryRepository.update(servicePrimaryModel);
    }

    /***
     * Deletes a ServicePrimaryModel object in Room
     * @param servicePrimaryModel - The object to delete
     */
    public void delete(ServicePrimaryModel servicePrimaryModel){
        servicePrimaryRepository.delete(servicePrimaryModel);
    }

    /***
     * Deletes all ServicePrimaryModel objects in Room
     */
    public void deleteAll(){
        servicePrimaryRepository.deleteAll();
    }

    /***
     * Returns a list of all Services for the given user
     * @return - Observable ServicePrimary Model LiveData List
     */
    public LiveData<List<ServicePrimaryModel>> getServicePrimaryModels() {
        return servicePrimaryRepository.getServicePrimaryModels();
    }

    /***
     * Returns a specific ServicePrimaryModel object
     * @param serviceId - Integer Id of service to be fetched
     * @return - Returns an Observable ServicePrimaryModel object
     */
    public LiveData<ServicePrimaryModel> getService(int serviceId){
        return servicePrimaryRepository.getService(serviceId);
    }

    /***
     * Searches Room DB for ServicePrimaryModels satisfying the search query
     * @param query - Search query parameter to filter the list. This should also include wild card params.
     *              Eg: %query%
     * @return - Observable ServicePrimaryModel results
     */
    public LiveData<List<ServicePrimaryModel>> filterServices(String query){
        return servicePrimaryRepository.filterServices(query);
    }

    public void updateId(int oldId, int newId){
        servicePrimaryRepository.updateId(oldId, newId);
    }

}
