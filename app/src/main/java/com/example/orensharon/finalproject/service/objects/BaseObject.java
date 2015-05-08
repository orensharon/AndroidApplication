package com.example.orensharon.finalproject.service.objects;

/**
 * Created by orensharon on 12/27/14.
 */
public class BaseObject {

    private int mId;
    private String mTypeOfContent;
    private String mChecksum;

    public BaseObject(int id, String type, String checksum) {

        mId = id;
        mTypeOfContent = type;
        mChecksum = checksum;
    }

    public int getId() {
        return mId;
    }

    public String getTypeOfContent() {
        return mTypeOfContent;
    }

    public String getChecksum() {
        return mChecksum;
    }

}
