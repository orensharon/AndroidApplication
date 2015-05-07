package com.example.orensharon.finalproject.service;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.example.orensharon.finalproject.service.observers.BaseContentObserver;
import com.example.orensharon.finalproject.service.observers.ContactObserver;
import com.example.orensharon.finalproject.service.observers.PhotoObserver;
import com.example.orensharon.finalproject.service.upload.helpers.NetworkChangeReceiver;
import com.example.orensharon.finalproject.service.upload.helpers.SyncUpdateMessage;
import com.example.orensharon.finalproject.sessions.SettingsSession;

import java.util.Observable;
import java.util.Observer;


/**
 * Created by orensharon on 11/30/14.
 * This is implantation of a background service.
 * Service job is to register to a content observers according to the
 * user's selection.
 */
public class ObserverService extends Service implements Observer {



    // Service Status
    public static final int STATUS_SERVICE_NOT_RUNNING = 0;
    public static final int STATUS_SERVICE_RUNNING = 1;
    public static final int STATUS_SERVICE_ERROR = 2;


    // Messaging with the settings activity constants
    public static final String TYPE_OF_MESSAGE_FROM_SERVICE_KEY = "type_of_message"; // The type of the message
    public static final String ERROR_CODE_FROM_SERVICE_KEY = "error_from_service_msg";
    public static final String PROGRESS_CODE_FROM_SERVICE_KEY = "progress_from_service_msg";
    public static final String EXTRA_MESSAGE_FROM_SERVICE_KEY = "extra_message_from_service_msg";

    // Message types
    public static final int MESSAGE_FROM_SERVICE_ERROR = 1;
    public static final int MESSAGE_FROM_SERVICE_COMM = 2;
    public static final int MESSAGE_FROM_SERVICE_PROGRESS = 3;

    // Internal codes
    public static final int SYNC_DONE = 20;
    public static final int SYNC_MIGHT_DONE_WITH_ERROR = 21;

    // Internal error code
    public static final int SAFE_UNREACHABLE = 40;
    public static final int NO_INTERNET = 30;



    // Service status constants
    private final String SERVICE_RUNNING_MSG = "Service is running";
    private final String SERVICE_NOT_RUNNING_MSG = "Service is not running";

    // Observers Uri constants
    private static final Uri PHOTO_OBSERVER_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final Uri CONTACT_OBSERVER_URI = ContactsContract.Contacts.CONTENT_URI;


     // Service status is not running (as default)
    private static int mServiceStatus = STATUS_SERVICE_NOT_RUNNING;

    // To communicate with the main thread
    private LocalBroadcastManager mBroadcaster;

    // The concrete observers
    private BaseContentObserver mPhotosObserver, mContactsObserver;

    // Session
    private SettingsSession mSettingsSession;

    private static boolean mIsInternetObserving = false;

    @Override
    public void onCreate() {

        // Upon service creation:
        // 1. Init broadcaster - an helper to communicate with the activity

        super.onCreate();
        mBroadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public void onDestroy() {

        // Stop the service by user or by the OS
        // Upon service creation:
        // 1. Set service status to not running
        // 2. Send a broadcast with new service status
        // 3. Unregister from content observer
        // 4. Disposing the upload manager

        super.onDestroy();




        mServiceStatus = STATUS_SERVICE_NOT_RUNNING;

        sendResult(2, STATUS_SERVICE_NOT_RUNNING, SERVICE_NOT_RUNNING_MSG);

        // Unregister from services
        this.getApplicationContext().getContentResolver()
                .unregisterContentObserver(mPhotosObserver);


        this.getApplicationContext().getContentResolver()
                .unregisterContentObserver(mContactsObserver);

        if (mIsInternetObserving) {
            UnregisterNetworkObserver();
        }

        mContactsObserver.Destroy();
        mPhotosObserver.Destroy();
        Log.i("sharonlog","Service destroyed");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Service was started

        String message = null;
        mSettingsSession = new SettingsSession(getApplicationContext());

        super.onStartCommand(intent, flags, startId);


        // If the service is started at the first time - send message its running
        if (mServiceStatus == STATUS_SERVICE_NOT_RUNNING) {
            mServiceStatus = STATUS_SERVICE_RUNNING;
            message = SERVICE_RUNNING_MSG;

            // TODO: read from session and register to observers
            // Init the concrete observers
            mContactsObserver = new ContactObserver(this, CONTACT_OBSERVER_URI);
            mPhotosObserver = new PhotoObserver(this, PHOTO_OBSERVER_URI);

            RegisterToObserver(CONTACT_OBSERVER_URI, mContactsObserver);
            RegisterToObserver(PHOTO_OBSERVER_URI, mPhotosObserver);



            Log.i("sharonlog","SERVICE STARTED");
        } else if (mServiceStatus == STATUS_SERVICE_RUNNING) {

            // If service is already running - manage the content

            Log.i("sharonlog","SERVICE UPDATING...");

            mContactsObserver.Manage();
            mPhotosObserver.Manage();

        }

        // Register to internet connection observer
        if (!mIsInternetObserving) {
            RegisterNetworkObserver();
        } else {
            UnregisterNetworkObserver();
        }


        // Set the status anyway
        mServiceStatus = STATUS_SERVICE_RUNNING;
        sendResult(2, STATUS_SERVICE_RUNNING, message);




        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }



    private void RegisterToObserver(Uri uri, BaseContentObserver observer) {

        // Register a given observer according to the given uri

        this.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        uri, false,
                        observer);
    }



    public void sendResult(int resultType, int code, String message) {

        // Send a broadcast to the loader activity

        Intent intent;

        intent = new Intent(ObserverService.class.getName());
        if (message != null)
        {
            if (resultType == MESSAGE_FROM_SERVICE_ERROR) {
                // Means error
                intent.putExtra(ERROR_CODE_FROM_SERVICE_KEY, code);
                intent.putExtra(TYPE_OF_MESSAGE_FROM_SERVICE_KEY, MESSAGE_FROM_SERVICE_ERROR);
            } else if (resultType == MESSAGE_FROM_SERVICE_PROGRESS) {
                // Means progress
                intent.putExtra(PROGRESS_CODE_FROM_SERVICE_KEY, code);
                intent.putExtra(TYPE_OF_MESSAGE_FROM_SERVICE_KEY, MESSAGE_FROM_SERVICE_PROGRESS);
            //} else if (resultType == 2) {
                // Means comm
                //intent.putExtra(MSG_FROM_SERVICE, code);
            }
            intent.putExtra(EXTRA_MESSAGE_FROM_SERVICE_KEY, message);
        }

        // Send the broadcast message
        mBroadcaster.sendBroadcast(intent);
    }

    public void sendError(int errorCode) {

        String message = "some error";


        if (errorCode == SAFE_UNREACHABLE) {
            message = "Check if safe in online..";
        } else if (errorCode == NO_INTERNET) {
            message = "Check internet connection..";
        }
        sendResult(MESSAGE_FROM_SERVICE_ERROR, errorCode, message);
    }
    public void sendProgress(int progressCode) {

        String message = "some progress";

        if (progressCode == SYNC_DONE) {
            message = "Sync done";
        } else if (progressCode == SYNC_MIGHT_DONE_WITH_ERROR) {
            message = "Sync done, but was unable to locate some local resource";
        }
        sendResult(MESSAGE_FROM_SERVICE_PROGRESS, progressCode, message);
    }

    public static int getServiceStatus() {

        // Getter of service status

        return mServiceStatus;
    }



    // Internet observer

    public void RegisterNetworkObserver() {

        // Register to the internet connection observer
        NetworkChangeReceiver.getObservable().addObserver(this);
        mIsInternetObserving = true;
        Log.i("sharonlog","Registered to internet observing");
    }
    public void UnregisterNetworkObserver() {
        // Unregister to the internet connection observer

        Log.i("sharonlog","Unregistered from internet observing");
        NetworkChangeReceiver.getObservable().deleteObserver(this);
        mIsInternetObserving = false;
    }

    @Override
    public void update(Observable observable, Object data) {

        // Whenever connection changes, this method will be called and determine the internet status

        int internetStatus;
        SyncUpdateMessage msg;
        msg = ExtractMessageFromData(data);

        if (msg == null) {
            return;
        }


        internetStatus = (Integer)msg.getData();

        if (internetStatus == NetworkChangeReceiver.NOT_CONNECTED) {

        } else {
            // Internet is connected
            Log.e("sharonlog","Internet connection changed : " +  internetStatus);
           // mManager.HandleUnsyncedContent();
        }


    }

    private SyncUpdateMessage ExtractMessageFromData(Object data) {

        SyncUpdateMessage msg;

        if (data instanceof SyncUpdateMessage) {
            msg = (SyncUpdateMessage) data;
        } else {
            return null;
        }

        if (msg.getMessageCode() != SyncUpdateMessage.SYNC_SUCCESSFUL) {
            return null;
        }

        return msg;
    }


}
