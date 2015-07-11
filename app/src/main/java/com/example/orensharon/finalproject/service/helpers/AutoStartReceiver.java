package com.example.orensharon.finalproject.service.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.sessions.SettingsSession;

/**
 * Created by orensharon on 7/11/15.
 */
public class AutoStartReceiver extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        SettingsSession settingsSession = new SettingsSession(arg0);

        // Auto start service if the user has enabled it
        if (settingsSession.getServiceIsEnabledByUser()) {
            Intent intent = new Intent(arg0, ObserverService.class);
            arg0.startService(intent);
            Log.i("sharonlog", "started");
        }
    }
}