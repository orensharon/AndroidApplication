package com.example.orensharon.finalproject.sessions;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.orensharon.finalproject.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void AddBackupedDataList(String contentKey, String itemKey, String itemValue) {

        String temp;
        Map<String, String> list;

        temp = mSharedPreferences.getString(contentKey,"");
        list = Helper.StringToMap(temp);
        list.put(itemKey, itemValue);

        mEditor.putString(contentKey, list.toString());
        mEditor.apply();
    }

    public void UpdateContentFromData(String contentKey, String itemKey, String itemValue) {

        String temp;
        Map<String, String> list;

        temp = mSharedPreferences.getString(contentKey,"");
        list = Helper.StringToMap(temp);
        list.remove(itemKey);
        list.put(itemKey, itemValue);

        mEditor.putString(contentKey, list.toString());
        mEditor.apply();
    }


    public void AddToUnsyncedList(String contentKey, String id) {


        List<String> list;

        list = Helper.StringToList(mSharedPreferences.getString(contentKey,""));

        // Check if the the id is already stored in the list
        if (!list.contains(id)) {
            list.add(id);

            mEditor.putString(contentKey, list.toString());
            mEditor.apply();
        }



    }

    public void RemoveFromUnsyncedList(String contentKey, String id) {

        List<String> list;

        list = Helper.StringToList(mSharedPreferences.getString(contentKey,""));

        // Check if the the id is already stored in the list
        if (list.contains(id)) {
            list.remove(id);

            mEditor.putString(contentKey, list.toString());
            mEditor.apply();
        }
    }

    public Map<String,String> getToBackupList(String contentKey) {
        return Helper.StringToMap(mSharedPreferences.getString(contentKey,""));
    }

    public List<String> getUnsyncedList(String contentKey) {
        return Helper.StringToList(mSharedPreferences.getString(contentKey,""));
    }








}
