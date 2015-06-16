package com.example.orensharon.finalproject.gui.settings;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.orensharon.finalproject.gui.IFragment;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.helpers.ObserverServiceBroadcastReceiver;

public class SettingsActivity extends FragmentActivity implements IFragment {

    private ObserverServiceBroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        createCustomActionBarTitle();

        mBroadcastReceiver = new ObserverServiceBroadcastReceiver(this);

        if (savedInstanceState == null) {
            LoadFragment(new SettingsFragment(),"SETTINGS_FRAGMENT", false);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver),
                new IntentFilter(ObserverService.class.getName()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:

                LoadFragment(new AccountFragment(),"ACCOUNT_FRAGMENT", true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void LoadFragment(Fragment fragment, String tag, boolean isSupportBack) {

        // The implementation of the IFragment interface

        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        if (isSupportBack) {
            fragmentTransaction.replace(R.id.fragment_container, fragment,tag).addToBackStack(null).commit();
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

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

        //if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(font);
        //}

    }


}
