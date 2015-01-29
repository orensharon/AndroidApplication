package com.example.orensharon.finalproject.service.upload.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Observable;

/**
 * Created by orensharon on 1/17/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final int NOT_CONNECTED = 0;

    private static int mConnectionStatus;

    @Override
    public void onReceive(final Context context, final Intent intent) {


        String action;
        action = intent.getAction();

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            Log.d("NetworkChangeReceiver", "Connection status changed");
            UpdateInternetStatus(context);

        }


    }

    public static void UpdateInternetStatus(Context context) {

        ConnectivityManager conMan;
        NetworkInfo netInfo;

        // Get the connectivity manager and get the net information
        conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMan.getActiveNetworkInfo();

        if (netInfo != null) {

            // TODO: When internet is available - learn more about connection type
            if (netInfo.isConnected()) {
                mConnectionStatus = netInfo.getType();
            } else {
                mConnectionStatus = NOT_CONNECTED;
            }

        } else {
            mConnectionStatus = NOT_CONNECTED;
        }

        getObservable().connectionChanged();
    }

    public static class NetworkObservable extends Observable {
        private static NetworkObservable instance = null;

        private NetworkObservable() {
            // Exist to defeat instantiation.
        }

        public void connectionChanged(){
            setChanged();
            notifyObservers(new SyncUpdateMessage(
                    SyncUpdateMessage.SYNC_SUCCESSFUL, 0, mConnectionStatus));
        }

        public static NetworkObservable getInstance(){
            if(instance == null){
                instance = new NetworkObservable();
            }
            return instance;
        }
    }

    public static NetworkObservable getObservable() {
        return NetworkObservable.getInstance();
    }
}
