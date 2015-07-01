package com.example.orensharon.finalproject.service.observers;

import android.content.Context;
import android.net.Uri;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.service.managers.PhotoManager;

/**
 * Created by orensharon on 12/10/14.
 * * This is the photos deactivate class from base content observer

 */
public class PhotoObserver extends BaseContentObserver {

    public PhotoObserver(Context context, Uri uri) {
        super(context);

        mContentType = ApplicationConstants.TYPE_OF_CONTENT_PHOTO;
        // Create a manager for that content. The manager will monitor the change of the content
        mManager = new PhotoManager(mContext, uri, mContentType);
        this.Manage();
    }


}
