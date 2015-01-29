package com.example.orensharon.finalproject.service.upload;

import android.content.Context;
import android.util.Log;

import com.example.orensharon.finalproject.logic.RequestFactory;

import com.example.orensharon.finalproject.service.upload.helpers.SyncUpdateMessage;

import com.example.orensharon.finalproject.service.upload.helpers.NetworkChangeReceiver;
import com.example.orensharon.finalproject.sessions.SystemSession;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by orensharon on 12/30/14.
 */
public class UploadManager implements Observer {


    private Context mContext;
    private RequestFactory mRequestFactory;

    public UploadManager(Context context) {

        mContext = context;
        mRequestFactory = new RequestFactory(context);

        // Register to the network observer.
        // The main job of this observer is once the internet connection is online
        // To sync the unsynced items

        //RegisterNetworkObserver();

        //NetworkChangeReceiver.UpdateInternetStatus(mContext);
    }

    public void Dispose() {

        Log.i("Disposing upload manager","...");
        //UnregisterNetworkObserver();
    }

    public void RegisterNetworkObserver() {

        // Register to the internet connection observer
        NetworkChangeReceiver.getObservable().addObserver(this);
    }
    public void UnregisterNetworkObserver() {
        // Unregister to the internet connection observer

        Log.i("Unregister NetworkObserver","");
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {

        // Whenever connection changes, this method will be called and determine the internet status

        int internetStatus;
        SyncUpdateMessage msg;
        msg = ExtractMessageFromData(data);

        if (msg == null) {
            return;
        }


        internetStatus = (Integer)msg.getData();

        if (internetStatus == NetworkChangeReceiver.NOT_CONNECTED) {

        } else {
            // Internet is connected

        }


    }

    private SyncUpdateMessage ExtractMessageFromData(Object data) {

        SyncUpdateMessage msg;

        if (data instanceof SyncUpdateMessage) {
            msg = (SyncUpdateMessage) data;
        } else {
            return null;
        }

        if (msg.getMessageCode() != SyncUpdateMessage.SYNC_SUCCESSFUL) {
            return null;
        }

        return msg;
    }

    public void AddToPool(Object newContent) {

        SystemSession systemSession = new SystemSession(mContext);


        String ip = systemSession.getRemoteIPAddress();
        String url = "http://" + ip + ":9003/StreamService/Upload/";
        //mRequestFactory.CreatePostRequest(url, new ContentResponse(mContext));
    }
}
