package com.roninaks.enqueue.helpers;

import org.json.JSONObject;

public abstract class EntityxBridge {
    protected abstract void importFromJson(JSONObject jsonObject) throws Exception;
    public abstract JSONObject exportToJson() throws Exception;
}
