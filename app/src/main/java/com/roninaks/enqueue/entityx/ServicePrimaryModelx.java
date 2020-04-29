package com.roninaks.enqueue.entityx;

import com.roninaks.enqueue.annotations.BindField;
import com.roninaks.enqueue.helpers.EntityX;
import com.roninaks.enqueue.models.QueueModel;

import java.util.ArrayList;

public class ServicePrimaryModelx extends EntityX {
    @BindField("s_id") public int ServiceId;
    @BindField("u_id") public int UserId;
    @BindField("name") public String ServiceName;
    @BindField("q_waiting") public int WaitingCount;
    @BindField("avg_time") public int AvgWaitingTime;
    @BindField("q_served") public int Served;
    @BindField("description") public String Description;
    @BindField("slug") public String Slug;
    @BindField("q_limit") public int QueueLimit;
    @BindField("wait_limit") public int WaitLimit;
    @BindField("status") public String Status;
    //    private Date ServiceDate;
    public ArrayList<QueueModel> QueueModels;

    public ServicePrimaryModelx() {
        super();
    }
}

