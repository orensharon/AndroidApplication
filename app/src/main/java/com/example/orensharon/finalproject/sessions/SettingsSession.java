package com.example.orensharon.finalproject.sessions;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orensharon on 1/28/15.
 * This class represents the session of the settings contains:
 *  1. if the service is running
 *  2. the content items that the user chose to backup
 */

public class SettingsSession {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    // Preferences key constants
    private final String IS_SERVICE_ENABLED_BY_USER = "IS_SERVICE_ENABLED_BY_USER";
    private final String IS_WIFI_ONLY = "IS_WIFI_ONLY";
    private final String SESSION_NAME = "SETTINGS_SESSION";

    public SettingsSession(Context context) {

        mContext = context;

        // Get the shared preferences from the system
        mSharedPreferences = mContext.getSharedPreferences(SESSION_NAME, mContext.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    // User preference to enable the content observation service
    public void setServiceIsEnabledByUser(boolean flag) {

        // From a given remote enable state flag save the user preference
        mEditor.putBoolean(IS_SERVICE_ENABLED_BY_USER, flag);
        mEditor.apply();
    }
    public boolean getServiceIsEnabledByUser() {

        // By default the service is off
        return mSharedPreferences.getBoolean(IS_SERVICE_ENABLED_BY_USER, false);
    }

    // Setter and getter for the content list items
    public void setUserContentItem(String key, boolean value) {

        // When the user clicks on items on the contents list in the settings screen
        // It will toggle this method with the value of the checkbox

        mEditor.putBoolean(key, value);
        mEditor.apply();
    }
    public boolean getUserContentItem(String key) {

        // If no settings exist then set as false
        return mSharedPreferences.getBoolean(key, true);
    }

    // Setter and getter for the wifi only option
    public void setWIFIOnly(boolean flag) {
        // From a given state flag save the user preference
        mEditor.putBoolean(IS_WIFI_ONLY, flag);
        mEditor.apply();
    }
    public boolean getWIFIOnly() {
        // Option is on (true) by default
        return mSharedPreferences.getBoolean(IS_WIFI_ONLY, true);
    }


}
