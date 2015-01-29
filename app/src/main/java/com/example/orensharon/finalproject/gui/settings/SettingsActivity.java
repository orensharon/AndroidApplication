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

import com.example.orensharon.finalproject.gui.login.LoginActivity;
import com.example.orensharon.finalproject.gui.settings.controls.CheckboxAdapter;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.settings.controls.Content;
import com.example.orensharon.finalproject.services.contentobserver.ObserverService;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;

import java.util.ArrayList;

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


        Switch mySwitch;
        mySwitch = (Switch) findViewById(R.id.switch_enable_service);

        // Set the switch according what the user saved preference, by default will be false
        mySwitch.setChecked(mSettingsSession.getServiceIsEnabledByUser());
        EnableSwitchListener(mySwitch);

        // Check the current state before we display the screen
        mContentsListView.setVisibility( ((mySwitch.isChecked()==true) ? View.VISIBLE : View.INVISIBLE) );
        LoadContentOptionsIntoListView();
    }

    private void EnableSwitchListener(Switch mySwitch) {
        // Attach a listener to check for changes in state
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
        Intent ServiceIntent;
        ServiceIntent = new Intent(getApplicationContext(), ObserverService.class);


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
                LogOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LogOut() {

        SystemSession systemSession;
        systemSession = new SystemSession(getApplicationContext());
        systemSession.Logout();
        LoadLoginActivity();
    }

    private void LoadLoginActivity() {

        Intent intent;
        intent = new Intent(getApplicationContext(), LoginActivity.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Staring Login Activity
        startActivity(intent);

        // Finish this activity
        finish();
    }

    private void LoadContentOptionsIntoListView() {

        // This method will load all the content backup options
        // Into the list view

        Resources res;
        ArrayList<Content> contents;
        String[] options;


        res = getApplicationContext().getResources();

        // Reading items from contents xml resource
        contents = new ArrayList<Content>();
        options = res.getStringArray(R.array.contenttypes);

        // Add each item the list view
        for (String option : options) {

            // Read the saved setting of this content from the system saved data
            contents.add(new Content(option));
        }

        mBoxAdapter = new CheckboxAdapter(this, contents);
        mContentsListView.setAdapter(mBoxAdapter);

    }
}
