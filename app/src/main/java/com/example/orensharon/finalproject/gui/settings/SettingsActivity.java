package com.example.orensharon.finalproject.gui.settings;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
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

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.gui.IFragment;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.helpers.ObserverServiceBroadcastReceiver;

public class SettingsActivity extends FragmentActivity implements IFragment {

    private ObserverServiceBroadcastReceiver mBroadcastReceiver;

    public ObserverService mObserverService;
    public boolean mBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        createCustomActionBarTitle();

        mBound = false;

        mBroadcastReceiver = new ObserverServiceBroadcastReceiver(this);

        if (savedInstanceState == null) {
            LoadFragment(new SettingsFragment(), ApplicationConstants.SETTING_FRAGMENT_TAG, false);
        }


    }

    public void startObservingService() {

        // Starting the observer service

        Intent intent = new Intent(this, ObserverService.class);
        bindService(intent, mConnection, this.BIND_AUTO_CREATE);
        startService(intent);



    }

    public void stopObservingService() {

        // Stopping the observer service
        if (mBound) {

            // Unbind from the service
            unbindService(mConnection);

            // Stop the service
            Intent intent = new Intent(this, ObserverService.class);
            stopService(intent);
            mBound = false;
        }

    }

    public void Evil() {
        mObserverService.Evil();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to ObserverService
        Intent intent = new Intent(this, ObserverService.class);
        bindService(intent, mConnection, this.BIND_AUTO_CREATE);



        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver),
                new IntentFilter(ObserverService.class.getName()));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {

            // Unbind from the service
            unbindService(mConnection);
            mBound = false;
        }

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

                LoadFragment(new AccountFragment(),ApplicationConstants.ACCOUNT_FRAGMENT_TAG, true);
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


        actionBarTitleView.setTypeface(font);


    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ObserverService.ServiceBinder binder = (ObserverService.ServiceBinder) service;
            mObserverService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
