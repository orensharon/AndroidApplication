package com.example.orensharon.finalproject.service.observers;

import android.content.Context;
import android.database.ContentObserver;
import android.util.Log;

import com.example.orensharon.finalproject.service.db.ContentBL;
import com.example.orensharon.finalproject.service.managers.BaseManager;
import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.sessions.ContentSession;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by orensharon on 12/13/14.
 * This class is the base of the content observer module
 */
abstract public class BaseContentObserver extends ContentObserver {

    public Context mContext;
    protected BaseManager mManager;
    protected String mContentType;

    private int initialPos;
    private long mLastTimeOfCall = 0L;
    private long mLastTimeOfUpdate = 0L;
    private long threshold_time = 5000;

    public BaseContentObserver(Context context) {

        super(null);
        mContext = context;
        //initialPos = getLastMsgId();

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

                    //ReentrantLock lock = new ReentrantLock();
                    BaseObject content;


                    //lock.lock();
                    content = mManager.HandleContent();


                    if (content != null) {
                        Log.e("sharonlog", "Found (new/edit) content!" + content.getTypeOfContent() + " ID:" + content.getId());


                        //if (initialPos != getLastMsgId()) {
                        // Sending the content into the upload pool
                        mManager.getUploadManager().DispatchRequest(content, false);
                        //    initialPos = content.getId();
                        //}
                    } else {
                        Log.e("sharonlog", "No content found");
                    }
                    //lock.unlock();

                }
            }).start();

            mLastTimeOfUpdate = System.currentTimeMillis();

        }


    }


    public int getLastMsgId() {
        ContentSession contentSession = new ContentSession(mContext);

        return contentSession.getLatestId(mContentType);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    public void Manage() {
        mManager.Manage();
    }

    public void Sync() {
        mManager.Sync();
    }

    public void CancelSyncing() {
        mManager.CancelSyncing();
    }

}
