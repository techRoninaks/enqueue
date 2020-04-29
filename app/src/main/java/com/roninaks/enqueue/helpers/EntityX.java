package com.roninaks.enqueue.helpers;

import com.roninaks.enqueue.annotations.BindField;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public abstract class EntityX {

    static class EntityMetaData {
        String key;
        Class entityType;
        Class<?> collectionType;
        boolean isArray = false;
        boolean isDerivedFromEntity;
        EntityMetaData(String key, Class entityType, Class collectionType) {
            this.key = key;
            this.entityType = entityType;
            this.collectionType = collectionType;
            isArray = true;
            isDerivedFromEntity = EntityX.class.isAssignableFrom(entityType);
        }
        EntityMetaData(String key, Class entityType) {
            this.key = key;
            this.entityType = entityType;
            this.collectionType = void.class;
            isDerivedFromEntity = EntityX.class.isAssignableFrom(entityType);
        }
        public Class getEntityType() {
            return entityType;
        }
    }

    protected HashMap<String, EntityMetaData> fields;

    void bind(EntityX clzz) {
        try {
            fields = new HashMap<>();
            for (Field field : clzz.getClass().getDeclaredFields()) {
                final BindField bf = field.getAnnotation(BindField.class);
                if (bf != null) {
                    String field_name = bf.value();
                    Class clz = field.getType();
                    if (Iterable.class.isAssignableFrom(clz)) {
                        Type type = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) type;
                        Type innerType = pt.getActualTypeArguments()[0];
                        Class<?> innerClass = (Class<?>) innerType;

                        fields.put(field.getName(), new EntityMetaData(field_name, innerClass, clz));
                    } else {
                        fields.put(field.getName(), new EntityMetaData(field_name, clz));
                    }
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    protected EntityX() {
        bind(this);
    }

    public JSONObject exportToJson() throws Exception{
        JSONObject outputJsonObject = new JSONObject();

        for (Field field : this.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if (fields.containsKey(fieldName)) {
                field.setAccessible(true);
                EntityMetaData t = fields.get(fieldName);
                Object fieldValue = field.get(this);
                if (fieldValue != null) {
                    if (t.isArray) {
                        Iterable iterable = (Iterable) fieldValue;
                        JSONArray array = new JSONArray();
                        if (t.isDerivedFromEntity) {
                            for (Object curr : iterable) {
                                array.put(((EntityX) curr).exportToJson());
                            }
                        } else {
                            for (Object curr : iterable) {
                                array.put(curr);
                            }
                        }
                        outputJsonObject.put(t.key, array);
                    } else {
                        if (t.isDerivedFromEntity) {
                            outputJsonObject.put(t.key, ((EntityX) fieldValue).exportToJson());
                        } else {
                            outputJsonObject.put(t.key, fieldValue);
                        }
                    }
                }
            }
        }
        return outputJsonObject;
    }

    public void importFromJson(JSONObject input) throws Exception{
        for (Field field : this.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            if (fields.containsKey(fieldName)) {
                field.setAccessible(true);
                EntityMetaData t = fields.get(fieldName);
                if (input.has(t.key)) {
                    if (t.isArray) {
                        JSONArray array = null;
                        try {
                            array = input.getJSONArray(t.key);
                        } catch (Exception E) {
                            array = null;
                        }
                        if (array != null) {
                            List list = (List) castList(t.collectionType);
                            if (t.isDerivedFromEntity) {
                                for (int i = 0; i < array.length(); i++) {
                                    list.add(castObject(array.getJSONObject(i), t.entityType));
                                }
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    Object data = array.get(i);
                                    list.add(data);
                                }
                            }
                            field.set(this, list);
                        }
                    } else {
                        if (t.isDerivedFromEntity) {
                            field.set(this, castObject(input.getJSONObject(t.key), t.entityType));
                        } else {
                            Object data = input.get(t.key);
                            field.set(this, data);
                        }
                    }
                }
            }
        }
    }

    protected  <T extends EntityX> T castObject(JSONObject obj, Class<T> type) throws Exception {
        if (obj != null) {
            T w = type.newInstance();
            w.importFromJson(obj);
            return w;
        } else {
            return null;
        }
    }

    protected  <T> T castList(Class<T> type) throws Exception {
        T w = type.newInstance();
        return w;
    }
}

