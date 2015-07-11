package com.example.orensharon.finalproject.service.db;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by orensharon on 5/7/15.
 * Object represents a content give from db
 */
public class DBContent {

    private int mId;
    private long mDateCreated, mDateModified;
    private String mType, mChecksum;
    private boolean mWasSynced, mIsSyncing, mIsReturnedError, mIsDirty;


    public DBContent(int id, String type, String checksum) {

        mId = id;
        mType = type;
        mChecksum = checksum;

    }

    public DBContent(int id, String type, String checksum, boolean wasSynced,
                     boolean isSyncing, boolean isReturnedError, boolean isDirty,
                     long dateCreated, long dateModified) {
        mId = id;
        mType = type;
        mChecksum = checksum;
        mWasSynced = wasSynced;
        mIsSyncing = isSyncing;
        mIsReturnedError = isReturnedError;
        mIsDirty = isDirty;
        mDateCreated = dateCreated;
        mDateModified = dateModified;
    }

    public String toString() {

        return "\nid:" + mId + "\ttype:" + mType + "\tchecksum:" + mChecksum + "\tsynced:" + mWasSynced +
                "\tisSyncing:" + mIsSyncing + "\treturnedError:" + mIsReturnedError + "\tisDirty:" + mIsDirty +
                "\tdateCreated:" + date(mDateCreated) + "\tdateModified:" + date(mDateModified);

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

    public long getDateCreated() {
        return mDateCreated;
    }

    public long getDateModified() {
        return mDateModified;
    }
    private String date(long time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String result = df.format(time);

        return  result;

    }

}
