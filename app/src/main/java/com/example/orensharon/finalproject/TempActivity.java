package com.example.orensharon.finalproject;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.helpers.ObserverServiceBroadcastReceiver;


public class TempActivity extends Activity {

    // Log
    private static TextView mLogTextView;
    private ObserverServiceBroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);

        mLogTextView = ((TextView)findViewById(R.id.LogTextView));
        mLogTextView.setMovementMethod(new ScrollingMovementMethod());



        WriteToLog("Loading application modules..");

        InitBroadcastReceiver();


        // Check if the status is already running or not and update mServiceStatus flag
        // according to the service status
        GetObserverServiceStatus();



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






    // Private Methods ############################################
    // ############################################################


    public void WriteToLog(String str) {

        // Write to application log the given str String
        if (str != null) {
            String value = mLogTextView.getText().toString();
            mLogTextView.setText(value + str + "\n");
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
