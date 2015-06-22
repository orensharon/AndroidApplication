package com.example.orensharon.finalproject.gui.feed.controls;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedPhotoItem {

    private int mID;
    private int mReadlPhotoId;
    private String mPhoto;
    private long mDateCreated;
    private String mGeoLocation;

    public FeedPhotoItem() {
        super();
    }

    public FeedPhotoItem(int id, int realPhotoId, String photo, String dateCreated, String geoLocation) {
        super();

        mID = id;
        mReadlPhotoId = realPhotoId;
        mPhoto = photo;
        mDateCreated = Long.parseLong(dateCreated);
        mGeoLocation = geoLocation;
    }

    public int getID() {
        return mID;
    }

    public int getRealPhotoId() {
        return mReadlPhotoId;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public long getDateCreated() { return mDateCreated; }

    public String getGeoLocation() { return mGeoLocation; }

}
