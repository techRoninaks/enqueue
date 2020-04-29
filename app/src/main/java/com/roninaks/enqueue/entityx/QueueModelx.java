package com.roninaks.enqueue.entityx;

import com.roninaks.enqueue.annotations.BindField;
import com.roninaks.enqueue.helpers.EntityX;

public class QueueModelx extends EntityX {
    @BindField("q_id") public int QueueId;
    @BindField("s_id") public int ServiceId;
    @BindField("name") public String UserName;
    @BindField("phone") public String PhoneNumber;
    @BindField("service_time") public long ServicedTime;
    @BindField("q_status") public String Status;
    @BindField("token") public int Token;
    @BindField("time") public String DateOfService;

    public QueueModelx() {
        super();
    }
}
