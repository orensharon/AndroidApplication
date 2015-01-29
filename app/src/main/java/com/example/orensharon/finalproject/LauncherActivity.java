package com.example.orensharon.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.gui.login.LoginActivity;
import com.example.orensharon.finalproject.sessions.SystemSession;

public class LauncherActivity extends Activity {

    private SystemSession mSystemSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSystemSession = new SystemSession(getApplicationContext());

        // If already logged in - show the feed activity
        if (mSystemSession.getLoginState() == true) {
            LoadActivity(FeedActivity.class);

        } else {
            LoadActivity(LoginActivity.class);
        }

        finish();
    }



    private void LoadActivity(Class cls) {

        Intent intent;
        intent = new Intent(getApplicationContext(), cls);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);
    }

}
