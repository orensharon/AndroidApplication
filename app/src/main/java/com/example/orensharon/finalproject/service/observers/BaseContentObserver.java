package com.example.orensharon.finalproject.service.observers;

import android.content.Context;
import android.database.ContentObserver;
import android.util.Log;

import com.example.orensharon.finalproject.service.managers.BaseManager;
import com.example.orensharon.finalproject.service.objects.BaseObject;


/**
 * Created by orensharon on 12/13/14.
 * This class is the base of the content observer module
 */
abstract public class BaseContentObserver extends ContentObserver {

    public Context mContext;
    protected BaseManager mManager;
    protected String mContentType;

    private long mLastTimeOfCall = 0L;
    private long mLastTimeOfUpdate = 0L;
    private long threshold_time = 5000;

    public BaseContentObserver(Context context) {

        super(null);
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {

        // Each registered content observer will fire this event on any change of the content
        // Checking if this is a new content and add it to the pool to send it


        mLastTimeOfCall = System.currentTimeMillis();

        // To prevent multiple calls
        if(mLastTimeOfCall - mLastTimeOfUpdate > threshold_time) {

            // Handling with content sampling using thread
            new Thread(new Runnable() {
                @Override
                public void run() {

                    BaseObject content;

                    content = mManager.HandleContent();

                    if (content != null) {
                        Log.e("sharonlog", "Found (new/edit) content!" + content.getTypeOfContent() + " ID:" + content.getId());



                        // Sending the content into the upload pool
                        mManager.getUploadManager().DispatchRequest(content, false);
                    } else {
                        Log.e("sharonlog", "No content found");
                    }
                }
            }).start();


            mLastTimeOfUpdate = System.currentTimeMillis();
        }
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    public void Manage() {

        mManager.Manage();
    }

    public void CancelSyncing() {
        mManager.CancelSyncing();
    }

}
