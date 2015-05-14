package com.example.orensharon.finalproject.gui.feed.controls;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedPhotoItem {

    private int mID;
    private String mPhoto;
    private String mDateCreated;

    public FeedPhotoItem() {
        super();
    }

    public FeedPhotoItem(int id, String photo, String dateCreated) {
        super();

        mID = id;
        mPhoto = photo;
        mDateCreated = dateCreated;
    }

    public int getID() {
        return mID;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getDateCreated() { return mDateCreated; }

}
