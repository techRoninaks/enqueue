package com.roninaks.enqueue.entityx;

import com.roninaks.enqueue.annotations.BindField;
import com.roninaks.enqueue.helpers.EntityX;

public class UserModelx extends EntityX {
    @BindField("id") public int UserId;
    @BindField("name") public String Name;
    @BindField("email") public String Email;
    @BindField("password") public String Password;
    @BindField("phone") public String Phone;
    @BindField("description") public String Description;
    @BindField("organisation") public String Organisation;
    @BindField("logo") public String Logo;

    public UserModelx() {
        super();
    }

}
