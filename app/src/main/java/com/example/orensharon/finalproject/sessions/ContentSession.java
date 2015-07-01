package com.example.orensharon.finalproject.sessions;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * Created by orensharon on 1/28/15.
 * This class represents the session of the settings contains:
 *  1. if the service is running
 *  2. the content items that the user chose to backup
 */

public class ContentSession {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    // Preferences key constants
    private final String SESSION_NAME = "CONTENT_SESSION";

    public ContentSession(Context context) {

        mContext = context;

        // Get the shared preferences from the system
        mSharedPreferences = mContext.getSharedPreferences(SESSION_NAME, mContext.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    public void setLatestId(String contentType, int value) {
        mEditor.putInt(contentType, value);
        mEditor.apply();
    }

    public int getLatestId(String contentType) {
        return mSharedPreferences.getInt(contentType, 0);
    }










}
