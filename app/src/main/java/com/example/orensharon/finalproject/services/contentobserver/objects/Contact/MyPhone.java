package com.example.orensharon.finalproject.services.contentobserver.objects.Contact;

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

}
