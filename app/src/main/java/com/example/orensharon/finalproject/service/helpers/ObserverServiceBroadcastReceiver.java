package com.example.orensharon.finalproject.service.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.orensharon.finalproject.TempActivity;
import com.example.orensharon.finalproject.service.ObserverService;

// TODO: Service to update UI - is this the right approach?

/**
 * Created by orensharon on 12/11/14.
 * This class implementation is a service feature which giving the ability
 * to communicate with the main application activity. i.e: Update the UI.
 */
public class ObserverServiceBroadcastReceiver extends BroadcastReceiver {

    private Context mMainContext;

    public ObserverServiceBroadcastReceiver(Context context) {


        mMainContext = context;
    }
    public void onReceive(Context context, Intent intent) {

        // The service sends messages to the main thread
        // This method will be the receiver of the messages

        String message;
        TempActivity main;

        message = intent.getStringExtra(ObserverService.MSG_FROM_SERVICE);
        main = (TempActivity)mMainContext;

        main.WriteToLog(message);
        main.UpdateUI();

    }
}
