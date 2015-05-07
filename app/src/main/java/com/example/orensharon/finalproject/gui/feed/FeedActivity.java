package com.example.orensharon.finalproject.gui.feed;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.settings.SettingsActivity;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 1/21/15.
 * This is the activity of the feed. It will request the data from the
 * pc server and will show the contents of the user.
 */
public class FeedActivity extends FragmentActivity implements IFragment {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        createCustomActionBarTitle();

        LoadFragment(new FeedManagerFragment(), "FEED_MANAGER_FRAGMENT", false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            LoadActivity(SettingsActivity.class, true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void LoadFragment(Fragment fragment,String tag, boolean isSupportBack) {

        // The implementation of the IFragment interface

        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        if (isSupportBack) {
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag).addToBackStack(null).commit();
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag).commit();
        }

    }

    @Override
    public void LoadActivity(Class cls, boolean isSupportBack) {

        // Load a given the activity

        Intent intent;
        intent = new Intent(this, cls);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);

        // Check if need to support the back button
        if (!isSupportBack) {
            // Close this activity
            finish();
        }
    }


    private void createCustomActionBarTitle(){

        // Customize the action bar title, fonts and alignments

        int actionBarTitle;
        TextView actionBarTitleView;

        actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");

        actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        actionBarTitleView.setTextColor(Color.WHITE);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LHANDW.TTF");

        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(font);
        }

    }



}
