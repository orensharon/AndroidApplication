package com.example.orensharon.finalproject.service.objects.Contact;

import com.example.orensharon.finalproject.ApplicationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 12/16/14.
 */
public class MyEmail {

    private String address;
    private String type;

    public MyEmail() {

    }

    public MyEmail(String address, String type) {
        super();
        this.address = address;
        this.type = type;
    }

    public String getEmail() {
        return address;
    }

    public void setEmail(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MyEmails [address=" + address + ", type=" + type + "]";
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ApplicationConstants.CONTACT_ADDRESS_ADDRESS_KEY,address);
            jsonObject.put(ApplicationConstants.CONTACT_ADDRESS_TYPE_KEY,type);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
