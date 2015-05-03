package com.example.orensharon.finalproject.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.orensharon.finalproject.ApplicationConstants;

/**
 * Created by orensharon on 1/26/15.
 * This class represents the session of the system contains:
 *  1. user's logged on/off state
 *  2. the ip the of the remote pc of the user
 */
public class SystemSession {

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private Context mContext;


    // Preferences key constants
    private final String SESSION_NAME = "SYSTEM_SESSION";
    private final String LOGIN_STATE = "LOGIN_STATE";
    private final String IP_ADDRESS = "IP_ADDRESS";
    private final String AUTH_TOKEN = "AUTH_TOKEN";


    public SystemSession(Context context) {

        mContext = context;

        // Get the shared preferences from the system
        mSharedPreferences = mContext.getSharedPreferences(SESSION_NAME, mContext.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    public void Login(String token) {

        // Set the system to a logged on state and save token

        setToken(token);
        setLoginState(true);

    }
    public void Logout() {

        // Set the system to a logged out state
        mEditor.putBoolean(LOGIN_STATE, false);

        setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);
        setToken(ApplicationConstants.NO_TOKEN_VALUE);

        mEditor.apply();
    }

    private void setLoginState(boolean state) {
        mEditor.putBoolean(LOGIN_STATE, true);
        mEditor.apply();
    }
    public boolean getLoginState() {

        // By default the state will be false, otherwise the saved state will be returned

        return mSharedPreferences.getBoolean(LOGIN_STATE, false);
    }

    public void setIPAddressOfSafe(String ipAddress) {

        // From a given remote ip address - store it in the system
        // It may be empty - this means there is no ip available

        mEditor.putString(IP_ADDRESS, ipAddress);
        mEditor.apply();
    }
    public String geIPAddressOfSafe() {

        // By default the ip will be NO_IP_VALUE string means there is no ip yet
        return mSharedPreferences.getString(IP_ADDRESS, ApplicationConstants.NO_IP_VALUE);
    }

    private void setToken(String token) {

        mEditor.putString(AUTH_TOKEN, token);
        mEditor.apply();
    }
    public String getToken() {

        // By default the token will be null means there is token
        return mSharedPreferences.getString(AUTH_TOKEN, ApplicationConstants.NO_TOKEN_VALUE);
    }
}
