package com.example.orensharon.finalproject.service.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.settings.SettingsActivity;
import com.example.orensharon.finalproject.gui.settings.SettingsFragment;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.sessions.SystemSession;


/**
 * Created by orensharon on 12/11/14.
 * This class implementation is a service feature which giving the ability
 * to communicate with the main application activity. i.e: Update the UI.
 */
public class ObserverServiceBroadcastReceiver extends BroadcastReceiver {

    private Context mContext;


    public ObserverServiceBroadcastReceiver(Context context) {
        mContext = context;
    }


    public void onReceive(Context context, Intent intent) {

        // The service sends messages to the main thread
        // This method will be the receiver of the messages

        int typeOfMessage;
        int code;

        SettingsActivity activity;
        activity = (SettingsActivity) mContext;

        SettingsFragment settingsFragment =
                (SettingsFragment)activity.getSupportFragmentManager().findFragmentByTag("SETTINGS_FRAGMENT");

        // Determine which kind of message the service send
        typeOfMessage = intent.getIntExtra(ObserverService.TYPE_OF_MESSAGE_FROM_SERVICE_KEY, 0);
        Button syncNowButton = (Button) activity.findViewById(R.id.sync_now_button);
        RelativeLayout syncNowButtonContainer = (RelativeLayout) activity.findViewById(R.id.sync_now_button_container);

        if  (typeOfMessage == ObserverService.MESSAGE_FROM_SERVICE_PROGRESS) {

            code = intent.getIntExtra(ObserverService.PROGRESS_CODE_FROM_SERVICE_KEY, 0);

            if (settingsFragment != null) {

                switch (code) {

                    case ObserverService.SYNC_START:
                        syncNowButton.setEnabled(false);
                        break;

                    case ObserverService.SYNC_DONE:
                        settingsFragment.initSyncButton();
                        break;

                    case ObserverService.SYNC_ERROR:
                        settingsFragment.initSyncButton();
                        break;

                    case ObserverService.SYNC_READY:
                        syncNowButton.setBackgroundResource(R.drawable.button_sync_now_selector);
                        settingsFragment.initSyncButton();
                        break;



                }
            }
        }
    }
}
