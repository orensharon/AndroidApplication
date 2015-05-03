package com.example.orensharon.finalproject.service.objects.Contact;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 12/15/14.
 */
public class MyInstantMessenger {
    
    private String name;
    private String type;

    public MyInstantMessenger() {
        super();
    }

    public MyInstantMessenger(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyInstantMessenger [name=" + name + ", type=" + type + "]";
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Name",name);
            jsonObject.put("Type",type);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}