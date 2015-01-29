package com.example.orensharon.finalproject.service.observers;

import android.content.Context;
import android.database.ContentObserver;

import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.managers.BaseManager;


/**
 * Created by orensharon on 12/13/14.
 * This class is the base of the content observer module
 */
abstract public class BaseContentObserver extends ContentObserver {

    public Context mContext;
    protected BaseManager mManager;


    public BaseContentObserver(Context context) {

        super(null);
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {

        // Each registered content observer will fire this event on any change of the content
        // Checking if this is a new content and add it to the pool to send it

        Object newContent;

        newContent = mManager.getNewContent();
        ObserverService.getUploadManager().AddToPool(newContent);

        super.onChange(selfChange);
    }

    public void Manage() {
        mManager.Manage();
    }
}
