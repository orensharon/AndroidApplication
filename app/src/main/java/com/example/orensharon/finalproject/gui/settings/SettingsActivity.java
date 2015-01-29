package com.example.orensharon.finalproject.gui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.gui.login.LoginActivity;
import com.example.orensharon.finalproject.gui.settings.controls.CheckboxAdapter;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.settings.controls.Content;
import com.example.orensharon.finalproject.services.contentobserver.ObserverService;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;

import java.util.ArrayList;
import java.util.Hashtable;

public class SettingsActivity extends Activity{

    private CheckboxAdapter mBoxAdapter;
    private ListView mContentsListView;

    private SettingsSession mSettingsSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSettingsSession = new SettingsSession(this);
        mContentsListView = (ListView) findViewById(R.id.content_list_view);


        Switch mySwitch = (Switch) findViewById(R.id.switch_enable_service);

        // Set the switch according what the user saved preference, by default will be false
        mySwitch.setChecked(mSettingsSession.getServiceIsEnabledByUser());

        // Check the current state before we display the screen
        mContentsListView.setVisibility( ((mySwitch.isChecked()==true) ? View.VISIBLE : View.INVISIBLE) );

        EnableSwitchListener(mySwitch);

        LoadContentOptionsIntoListView();
    }

    private void EnableSwitchListener(Switch mySwitch) {
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

            mContentsListView.setVisibility( ((isChecked==true) ? View.VISIBLE : View.INVISIBLE) );
                mSettingsSession.setServiceIsEnabledByUser(isChecked);


            if (isChecked == true) {
                // Start the service if the user has enabled it
                StartObservingService();


            } else {
                // Stop the service
                Intent mServiceIntent;
                mServiceIntent= new Intent(getApplicationContext(), ObserverService.class);
                stopService(mServiceIntent);
            }

            }
        });

    }

    private void StartObservingService() {

        // Creating an intent with the selected values of the user
        Intent ServiceIntent = new Intent(getApplicationContext(), ObserverService.class);

        // Service will start once, any call after that will only send
        // the intent to communicate with the service this way


        startService(ServiceIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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
                // Save Settings
                SaveSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveSettings() {
        // Creating hash table to hold a pair of content name and it's value
        // according to the selection of the user in the list view.

        // Create data structure from the selected check boxes to send to service
        Hashtable<String,Boolean> ContentToBackup = new Hashtable<String, Boolean>();

        // Iterate over the content items check box values
        // and create pair of the content name and the value of it in boolean
        // This hash table will be sent later with intent to the service
        for (Content c : mBoxAdapter.getBox()) {
            ContentToBackup.put(c.getTitle(), c.getChecked());
        }

        SystemSession systemSession = new SystemSession(getApplicationContext());
        systemSession.Logout();
        LoadLoginActivity();
    }

    private void LoadLoginActivity() {

        Intent intent;
        intent = new Intent(getApplicationContext(), LoginActivity.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);
        finish();
    }

    private void LoadContentOptionsIntoListView() {

        // This method will load all the content backup options
        // Into the list view, to user selection

        Context ctx = getApplicationContext();
        Resources res = ctx.getResources();

        //Hashtable<String,Boolean> savedSettings = mSystemSession.getUserContentSettings();

        // Reading items from contents xml resource
        ArrayList<Content> contents = new ArrayList<Content>();
        String[] options = res.getStringArray(R.array.contenttypes);

        // Add each item the list view
        for (String option : options) {
            boolean flag;

            // Read the saved setting of this content from the system saved data
            //flag = ((savedSettings.containsKey(option)) ? savedSettings.get(option) : false);
            contents.add(new Content(option, false));
        }

        mBoxAdapter = new CheckboxAdapter(this, contents);
        mContentsListView.setAdapter(mBoxAdapter);

    }
}
