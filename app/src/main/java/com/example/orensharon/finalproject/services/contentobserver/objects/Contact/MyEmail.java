package com.example.orensharon.finalproject.services.contentobserver.objects.Contact;

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

}
