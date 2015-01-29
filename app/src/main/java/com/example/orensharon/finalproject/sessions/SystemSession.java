package com.example.orensharon.finalproject.sessions;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by orensharon on 1/26/15.
 */
public class SystemSession {

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    private final String SESSION_NAME = "SYSTEM_SESSION";

    private final String LOGIN_STATE = "LOGIN_STATE";
    private final String IP_ADDRESS = "IP_ADDRESS";


    public SystemSession(Context context) {

        mContext = context;

        // Get the shared preferences from the system
        mSharedPreferences = mContext.getSharedPreferences(SESSION_NAME, mContext.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    // Login state setters and getters
    public void Login() {

        mEditor.putBoolean(LOGIN_STATE, true);
        mEditor.apply();
    }
    public void Logout() {

        mEditor.putBoolean(LOGIN_STATE, false);
        mEditor.apply();
    }
    public boolean getLoginState() {

        // By default the state will be false, otherwise the saved state will be return
        return mSharedPreferences.getBoolean(LOGIN_STATE, false);
    }


    // IP address of the remote pc setters and getters
    public void setRemoteIPAddress(String ipAddress) {

        // From a given remote ip address - store it in the system
        // It may be empty - this means there is no ip available
        mEditor.putString(IP_ADDRESS, ipAddress);
        mEditor.apply();
    }
    public String getRemoteIPAddress() {

        // By default the ip will be empty string means there is no ip yet
        return mSharedPreferences.getString(IP_ADDRESS, "");
    }


}
