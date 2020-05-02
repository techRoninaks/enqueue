package com.roninaks.enqueue.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.roninaks.enqueue.entityx.UserModelx;
import com.roninaks.enqueue.helpers.EntityxBridge;

import org.json.JSONObject;

import java.lang.reflect.Field;

@Entity
public class UserModel extends EntityxBridge {
    @PrimaryKey
    private int UserId;
    private String Name;
    private String Email;
    private String Password;
    private String Phone;
    private String Description;
    private String Organisation;
    private String Logo;

    public UserModel(){
        //Empty Constructor

    }

    public UserModel(JSONObject jsonObject) throws Exception{
        importFromJson(jsonObject);
    }
    //UserId
    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    //Name
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    //Email
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    //Password
    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    //Phone
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    //Description
    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    //Organisation
    public String getOrganisation() {
        return Organisation;
    }

    public void setOrganisation(String organisation) {
        Organisation = organisation;
    }

    //Logo
    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    @Override
    protected void importFromJson(JSONObject jsonObject) throws Exception {
        UserModelx userModelx = new UserModelx();
        userModelx.importFromJson(jsonObject);
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Field fieldx = userModelx.getClass().getDeclaredField(field.getName());
                field.set(this, fieldx.get(userModelx));
            }catch (NoSuchFieldException e){
                //Ignore the field
            }
        }
    }

    @Override
    public JSONObject exportToJson() throws Exception {
        UserModelx userModelx = new UserModelx();
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                Field fieldx = userModelx.getClass().getDeclaredField(field.getName());
                fieldx.set(userModelx, field.get(this));
            }catch (NoSuchFieldException e){
                //Ignore the field
            }
        }
        return userModelx.exportToJson();
    }

}
