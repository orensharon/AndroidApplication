package com.example.orensharon.finalproject.service.db;

/**
 * Created by orensharon on 5/7/15.
 */
public class DBContent {

    private int mId;
    private String mType, mChecksum;
    private boolean mWasSynced, mIsSyncing, mIsReturnedError;


    public DBContent(int id, String type, String checksum) {

        mId = id;
        mType = type;
        mChecksum = checksum;

    }

    public DBContent(int id, String type, String checksum, boolean wasSynced, boolean isSyncing, boolean isReturnedError) {
        mId = id;
        mType = type;
        mChecksum = checksum;
        mWasSynced = wasSynced;
        mIsSyncing = isSyncing;
        mIsReturnedError = isReturnedError;
    }

    public String toString() {

        return "\nid:" + mId + "\ttype:" + mType + "\tchecksum:" + mChecksum + "\tsynced:" + mWasSynced +
                "\tisSyncing:" + mIsSyncing + "\treturnedError:" + mIsReturnedError;
    }

    public int getId() {
        return  mId;
    }

    public String getType() {
        return mType;
    }

    public String getChecksum() {
        return mChecksum;
    }

}
