package com.roninaks.enqueue.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.roninaks.enqueue.models.UserModel;

@Dao
public interface UserDao {
    /***
     * Inserts usermodel object into the DB
     * @param userModel - Usermodel to be inserted
     */
    @Insert
    void insert(UserModel userModel);

    /***
     * Updates usermodel object in the DB
     * @param userModel - Usermodel to be updated
     */
    @Update
    void update(UserModel userModel);

    /***
     * Deletes usermodel object
     * @param userModel - Usermodel to be deleted
     */
    @Delete
    void delete(UserModel userModel);

    /***
     * Deletes all usermodels
     */
    @Query("DELETE FROM UserModel")
    void deleteAll();

    /***
     * Returns the UserModel object corresponding to the Id
     * @param userId - ID of the user
     * @return Returns null if does not exist otherwise returns UserModel
     */
    @Query("SELECT * FROM UserModel where UserId = :userId")
    LiveData<UserModel> getUser(int userId);

    /***
     * Returns the UserModel object corresponding to the Email
     * @param email Email Id of the user
     * @return Returns null if does not exist otherwise returns UserModel
     */
    @Query("SELECT * FROM UserModel where Email like :email")
    LiveData<UserModel> getUser(String email);

    /***
     * Checks whether user exists in the table
     * @param email - Email to check for user
     * @return 0 if user does not exist else returns a positive integer
     */
    @Query("SELECT COUNT(*) FROM UserModel where Email like :email")
    int hasUser(String email);
}
