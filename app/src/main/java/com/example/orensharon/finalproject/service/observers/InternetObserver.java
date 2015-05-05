package com.example.orensharon.finalproject.service.observers;

import android.util.Log;

import com.example.orensharon.finalproject.service.managers.BaseManager;
import com.example.orensharon.finalproject.service.upload.helpers.NetworkChangeReceiver;
import com.example.orensharon.finalproject.service.upload.helpers.SyncUpdateMessage;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by orensharon on 5/5/15.
 */
public class InternetObserver implements Observer {

    private BaseManager mManager;
    // Internet connection observer

    public void RegisterNetworkObserver(BaseManager manager) {

        mManager = manager;
        // Register to the internet connection observer
        NetworkChangeReceiver.getObservable().addObserver(this);
    }
    public void UnregisterNetworkObserver() {
        // Unregister to the internet connection observer

        Log.i("Unregister NetworkObserver", "");
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
            mManager.HandleUnsyncedContent();
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



}
