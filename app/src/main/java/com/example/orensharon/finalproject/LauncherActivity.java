package com.example.orensharon.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.gui.login.LoginActivity;
import com.example.orensharon.finalproject.sessions.SystemSession;

/**
 * This is an activity without any UI.
 * Upon creation it will check if the user's login session exist,
 * If exists then load the feed activity, otherwise load the login activity
 */
public class LauncherActivity extends Activity {

    private SystemSession mSystemSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the system session
        mSystemSession = new SystemSession(getApplicationContext());

        if (mSystemSession.getLoginState() == true) {
            // If already logged in - show the feed activity
            LoadActivity(FeedActivity.class);

        } else {
            // If not logged in - show the login activity
            LoadActivity(LoginActivity.class);
        }
    }


    private void LoadActivity(Class cls) {

        // Load a given the activity

        Intent intent;
        intent = new Intent(getApplicationContext(), cls);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);

        // Close this activity
        finish();
    }

}
