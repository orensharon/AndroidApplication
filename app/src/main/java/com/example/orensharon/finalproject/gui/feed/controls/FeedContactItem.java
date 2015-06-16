package com.example.orensharon.finalproject.gui.feed.controls;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedContactItem {

    private int mID;
    private String mDisplayName, mData;

    public FeedContactItem() {
        super();
    }

    public FeedContactItem(int id, String displayName, String data) {
        super();

        mID = id;
        mDisplayName = displayName;
        mData = data;

    }

    public int getID() {
        return mID;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
    public String getData() {
        return mData;
    }

}
