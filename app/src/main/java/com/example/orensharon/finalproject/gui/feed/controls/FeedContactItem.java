package com.example.orensharon.finalproject.gui.feed.controls;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedContactItem {

    private int mID;
    private String mDisplayName;

    public FeedContactItem() {
        super();
    }

    public FeedContactItem(int id, String displayName) {
        super();

        mID = id;
        mDisplayName = displayName;
    }

    public int getID() {
        return mID;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

}
