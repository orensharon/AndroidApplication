package com.example.orensharon.finalproject.service.objects.Contact;

/**
 * Created by orensharon on 12/15/14.
 */
public class MyAddress {

    private String address;
    private String type;

    public MyAddress() {
        super();
    }

    public MyAddress(String address, String type) {
        super();
        this.address = address;
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
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
        return "MyAddress [address=" + address + ", type=" + type + "]";
    }

}
