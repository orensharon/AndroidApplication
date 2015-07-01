package com.example.orensharon.finalproject.service.objects.Contact;

import com.example.orensharon.finalproject.ApplicationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 12/15/14.
 */
public class MyPhone {

    private String number;
    private String type;

    public MyPhone() {

    }

    public MyPhone(String number, String type) {
        super();
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyPhones [number=" + number + ", type=" + type + "]";
    }

    public String toJSONString() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ApplicationConstants.CONTACT_PHONE_NUMBER_KEY,number);
            jsonObject.put(ApplicationConstants.CONTACT_PHONE_TYPE_KEY,type);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
