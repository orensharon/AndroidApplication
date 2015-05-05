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
    private final String SESSION_NAME_KEY = "SYSTEM_SESSION_KEY";
    private final String LOGIN_STATE_KEY = "LOGIN_STATE_KEY";
    private final String USERNAME_KEY = "USERNAME_KEY";
    private final String PASSWORD_KEY = "PASSWORD_KEY";
    private final String IP_ADDRESS_KEY = "IP_ADDRESS_KEY";
    private final String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";


    public SystemSession(Context context) {

        mContext = context;

        // Get the shared preferences from the system
        mSharedPreferences = mContext.getSharedPreferences(SESSION_NAME_KEY, mContext.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    public void Login(String token,String username, String password) {

        // Set the system to a logged on state and save token

        setToken(token);
        setLoginState(true);
        setUsername(username);
        setPassword(password);

    }
    public void Logout() {

        // Set the system to a logged out state
        mEditor.putBoolean(LOGIN_STATE_KEY, false);

        setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);
        setToken(ApplicationConstants.NO_TOKEN_VALUE);

        mEditor.apply();
    }

    private void setLoginState(boolean state) {
        mEditor.putBoolean(LOGIN_STATE_KEY, true);
        mEditor.apply();
    }
    public boolean getLoginState() {

        // By default the state will be false, otherwise the saved state will be returned

        return mSharedPreferences.getBoolean(LOGIN_STATE_KEY, false);
    }

    public void setIPAddressOfSafe(String ipAddress) {

        // From a given remote ip address - store it in the system
        // It may be empty - this means there is no ip available

        mEditor.putString(IP_ADDRESS_KEY, ipAddress);
        mEditor.apply();
    }
    public String geIPAddressOfSafe() {

        // By default the ip will be NO_IP_VALUE string means there is no ip yet
        return mSharedPreferences.getString(IP_ADDRESS_KEY, ApplicationConstants.NO_IP_VALUE);
    }

    private void setToken(String token) {

        mEditor.putString(AUTH_TOKEN_KEY, token);
        mEditor.apply();
    }
    public String getToken() {

        // By default the token will be null means there is token
        return mSharedPreferences.getString(AUTH_TOKEN_KEY, ApplicationConstants.NO_TOKEN_VALUE);
    }


    private void setUsername(String username) {
        mEditor.putString(USERNAME_KEY, username);
        mEditor.apply();
    }
    public String getUsername(){

        return mSharedPreferences.getString(USERNAME_KEY, "");
    }

    private void setPassword(String password) {
        mEditor.putString(PASSWORD_KEY, password);
        mEditor.apply();
    }
    public String getPassword(){

        return mSharedPreferences.getString(PASSWORD_KEY, "");
    }
}
