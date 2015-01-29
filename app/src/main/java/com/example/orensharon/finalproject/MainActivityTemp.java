package com.example.orensharon.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.orensharon.finalproject.gui.settings.controls.CheckboxAdapter;
import com.example.orensharon.finalproject.gui.settings.controls.Content;
import com.example.orensharon.finalproject.services.contentobserver.ObserverService;
import com.example.orensharon.finalproject.services.contentobserver.helpers.ObserverServiceBroadcastReceiver;

import java.util.ArrayList;
import java.util.Hashtable;


public class MainActivityTemp extends Activity {

    // Constants
    public final static String APP_NAME = "com.example.orensharon.finalprojectandroid";
    private final static String USER_SETTINGS = "user_settings";

    // Log
    private static TextView mLogTextView;

    //ListView
    private CheckboxAdapter mBoxAdapter;
    private ListView mContentsListView;

    private SharedPreferences mSharedPreferences;
    private ObserverServiceBroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);

        mLogTextView = ((TextView)findViewById(R.id.LogTextView));
        mLogTextView.setMovementMethod(new ScrollingMovementMethod());

        mContentsListView = (ListView) findViewById(R.id.ContentTypesListView);

        WriteToLog("Loading application modules..");

        InitBroadcastReceiver();
        LoadContentOptionsIntoListView();

        // Check if the status is already running or not and update mServiceStatus flag
        // according to the service status
        GetObserverServiceStatus();

        // Get saved data from shared preferences, if exist
        GetDataFromSharedPreferences();

        // Enable / Disable to buttons and other UI components on screen
        UpdateUI();

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
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // this.unregisterReceiver(this.mBroadcastReceiver);
    }











    public void StartButton_OnClick(View view) {

        // Start the service and send the selected items to it.
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

        // Creating an intent with the selected values of the user
        Intent ServiceIntent = new Intent(this, ObserverService.class);
        ServiceIntent.putExtra(ObserverService.MSG_TO_SERVICE, ContentToBackup.toString());


        // Service will start once, any call after that will only send
        // the intent to communicate with the service this way
        MainActivityTemp.this.startService(ServiceIntent);


    }

    public void StopButton_OnClick(View view) {

        // Stop the service after clicking the button


        Intent mServiceIntent;

        mServiceIntent= new Intent(MainActivityTemp.this, ObserverService.class);
        MainActivityTemp.this.stopService(mServiceIntent);




    }












    // Private Methods ############################################
    // ############################################################


    public void WriteToLog(String str) {

        // Write to application log the given str String
        if (str != null) {
            String value = mLogTextView.getText().toString();
            mLogTextView.setText(value + str + "\n");
        }
    }

    private void LoadContentOptionsIntoListView() {

        // This method will load all the content backup options
        // Into the list view, to user selection

        Context ctx = getApplicationContext();
        Resources res = ctx.getResources();

        // Reading items from contents xml resource
        ArrayList<Content> contents = new ArrayList<Content>();
        String[] options = res.getStringArray(R.array.contenttypes);

        // Add each item the list view
        for (int i = 0; i < options.length ; i ++){
            contents.add(new Content(options[i], false));
        }

        mBoxAdapter = new CheckboxAdapter(this, contents);
        mContentsListView.setAdapter(mBoxAdapter);



    }

    private void GetDataFromSharedPreferences() {
        // Init shared preferences instance and read data if exists
        mSharedPreferences = getSharedPreferences(APP_NAME, MODE_PRIVATE);
        String contents = mSharedPreferences.getString(USER_SETTINGS,null);
        if (contents != null) {
           // Hashtable<String, Boolean> ContentsToBackUp = HelpMethods.StringToHashTable(contents);

        }
    }

    private void GetObserverServiceStatus() {
        // Get service status

        String message;
        if (ObserverService.getServiceStatus() == ObserverService.STATUS_SERVICE_RUNNING) {
            message = "Service is running";
        } else {
            message = "Service is not running";
        }

        // Update UI after getting service status
        WriteToLog(message);
    }

    private void InitBroadcastReceiver() {
        mBroadcastReceiver = new ObserverServiceBroadcastReceiver(this);
    }


    public void UpdateUI() {

        // Enable and disable the buttons and other components on the screen
        // according to the service status


        Button startButton = (Button)findViewById(R.id.StartButton);
        Button stopButton = (Button)findViewById(R.id.StopButton);
        ListView contentsListView = (ListView)findViewById(R.id.ContentTypesListView);

        if (ObserverService.getServiceStatus() == ObserverService.STATUS_SERVICE_NOT_RUNNING) {
            //stopButton.setEnabled(false);
            startButton.setEnabled(true);
            startButton.setText("Save and Start");
            contentsListView.setEnabled(true);
        } else if (ObserverService.getServiceStatus() == ObserverService.STATUS_SERVICE_RUNNING) {
            stopButton.setEnabled(true);
            startButton.setText("Save");
            contentsListView.setEnabled(true);
        }
    }


}
