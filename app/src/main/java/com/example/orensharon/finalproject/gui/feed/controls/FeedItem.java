package com.example.orensharon.finalproject.gui.feed.controls;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedItem {

    private int mID;
    private String mPhoto;

    public FeedItem() {
        super();
    }

    public FeedItem(int id, String photo) {
        super();

        mID = id;
        mPhoto = photo;
    }

    public int getID() {
        return mID;
    }

    public String getPhoto() {
        return mPhoto;
    }

}
