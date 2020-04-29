package com.roninaks.enqueue.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roninaks.enqueue.models.UserModel;
import com.roninaks.enqueue.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    /***
     * Inserts userModel to the database
     * @param userModel - Usermodel object to insert
     */
    public void insert(UserModel userModel){
        userRepository.insert(userModel);
    }

    /***
     * Updates userModel in the database
     * @param userModel - Usermodel object to insert
     */
    public void update(UserModel userModel){
        userRepository.update(userModel);
    }

    /***
     * Deletes userModel from the database
     * @param userModel - Usermodel object to insert
     */
    public void delete(UserModel userModel){
        userRepository.delete(userModel);
    }

    /***
     * Deletes all entries of Usermodel
     */
    public void deleteAll(){
        userRepository.deleteAll();
    }

    /***
     * Returns the UserModel object corresponding to the UserId
     * @param userId - UserId for the UserModel to be fetched
     * @return Returns null if userId is found. Otherwise returns UserModel object
     */
    public LiveData<UserModel> getUser(int userId){
        return userRepository.getUser(userId);
    }

    /***
     * Returns the UserModel object corresponding to the Email
     * @param email - Email id for the UserModel
     * @return Returns null if email is found. Otherwise returns UserModel object
     */
    public LiveData<UserModel> getUser(String email){
        return userRepository.getUser(email);
    }

}
