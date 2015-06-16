package com.example.orensharon.finalproject.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
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
import com.example.orensharon.finalproject.sessions.SystemSession;

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
    public static final int SYNC_START = 21;
    public static final int SYNC_ERROR = 22;
    public static final int SYNC_PAUSE = 23;

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
    private SystemSession mSystemSession;
    private static boolean mIsInternetObserving = false;


    private NotificationManager mNotificationManager;
    public final static int SYNC_NOTIFICATION = 10002;
    public final static int DETAILS_NOTIFICATION = 10003;

    @Override
    public void onCreate() {

        // Upon service creation:
        // 1. Init broadcaster - an helper to communicate with the activity

        super.onCreate();
        mBroadcaster = LocalBroadcastManager.getInstance(this);

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mSystemSession = new SystemSession(this);
        mSystemSession.setInSync(null,false);
        Log.e("sharontest","Service OnCreate: [" + mSystemSession.getInSync(null) +
        "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");

    }

    @Override
    public void onDestroy() {

        // Stop the service by user or by the OS

        super.onDestroy();

        mServiceStatus = STATUS_SERVICE_NOT_RUNNING;
        sendResult(2, STATUS_SERVICE_NOT_RUNNING, SERVICE_NOT_RUNNING_MSG);
        //mSystemSession.setInSync(null,false);

        // Canceling sync notification is exist
        cancelNotification(SYNC_NOTIFICATION);





        // Unregister from observers
        UnregisterFromObserver(mContactsObserver);
        UnregisterFromObserver(mPhotosObserver);

        mPhotosObserver = null;
        mContactsObserver = null;


        // Unregister from internet connection observer
        if (mIsInternetObserving) {
            UnregisterNetworkObserver();
        }



        Log.i("sharonlog","Service destroyed");


    }

    private void UnregisterFromObserver(BaseContentObserver contentObserver) {

        // Unregister from given observer
        // Also canceling existing request

        if (contentObserver != null) {

            contentObserver.CancelSyncing();

            Log.e("sharonlog","UnregisterFromObserver ");
            this.getApplicationContext().getContentResolver()
                    .unregisterContentObserver(contentObserver);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Service was started
        boolean sync = false;
        String message = null;
        mSettingsSession = new SettingsSession(getApplicationContext());




        super.onStartCommand(intent, flags, startId);

        // Read from intent, if a sync command then start the sync process
        if (intent != null && intent.getIntExtra("SYNC_COMMAND", 0) == 1) {
            sync = true;
            sendProgress(SYNC_START);
        }

        if (mSettingsSession.getUserContentItem("Contacts")) {

            // Phone book observing selected by user

            Log.i("sharonlog1","Phone Book is checked.");
            if (mContactsObserver == null) {

                // Observer in init for the first time
                Log.i("sharonlog1","mContactsObserver == null");
                Log.i("sharonlog1","register mContactsObserver");
                mContactsObserver = new ContactObserver(this, CONTACT_OBSERVER_URI);
                RegisterToObserver(CONTACT_OBSERVER_URI, mContactsObserver);

            } else {
                // Means service is running
             //   Log.i("sharonlog1","mContactsObserver != null");
             //   Log.i("sharonlog1","mContactsObserver manage");
                if (sync) {
                    mContactsObserver.Manage();
                }
            }

        } else {

            // Phone book observing unselected by user

            Log.i("sharonlog1","Phone Book is unchecked.");
            if (mContactsObserver != null) {
                // Unregister from observer
                Log.i("sharonlog1","mContactsObserver != null");
                Log.i("sharonlog1","unregister mContactsObserver");
                UnregisterFromObserver(mContactsObserver);

                mSystemSession.setInSync("Contact", false);
                Log.e("sharontest","Phone book uncheck: [" + mSystemSession.getInSync(null) +
                        "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");
                mContactsObserver = null;
            }

        }




        if (mSettingsSession.getUserContentItem("Photos")) {

            // Photos observing selected by user

            Log.i("sharonlog1","Photos is checked.");
            if (mPhotosObserver == null) {

                // Observer in init for the first time
                Log.i("sharonlog1","mPhotosObserver == null");
                Log.i("sharonlog1","register mPhotosObserver");
                mPhotosObserver = new PhotoObserver(this, PHOTO_OBSERVER_URI);
                RegisterToObserver(PHOTO_OBSERVER_URI, mPhotosObserver);
            } else {
                // Means service is running

                if (sync) {
                    mPhotosObserver.Manage();
                }
               // Log.i("sharonlog1","mPhotosObserver != null");
               // Log.i("sharonlog1","mPhotosObserver manage");
               // mPhotosObserver.Manage();
            }

        } else {

            // Photo observing unselected by user

            Log.i("sharonlog1","Photo is unchecked.");
            if (mPhotosObserver != null) {
                // Unregister from observer
                Log.i("sharonlog1","mPhotosObserver != null");
                Log.i("sharonlog1","unregister mPhotosObserver");
                UnregisterFromObserver(mPhotosObserver);
                mSystemSession.setInSync("Photo", false);
                Log.e("sharontest","Photo uncheck: [" + mSystemSession.getInSync(null) +
                        "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");
                mPhotosObserver = null;
            }

        }

        // Hide the notification status
        Log.e("sharontest","Service before fix: [" + mSystemSession.getInSync(null) +
                "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");


        if (!mSystemSession.getInSync("Photo") && !mSystemSession.getInSync("Contact"))  {
            mSystemSession.setInSync(null, false);
        }

        // Hide the notification status
        Log.e("sharontest","Service check if can remove noti: [" + mSystemSession.getInSync(null) +
                "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");


        if (!mSystemSession.getInSync(null)) {
            cancelNotification(SYNC_NOTIFICATION);
        }

        // If the service is started at the first time - send message its running
        if (mServiceStatus == STATUS_SERVICE_NOT_RUNNING) {
            mServiceStatus = STATUS_SERVICE_RUNNING;
            message = SERVICE_RUNNING_MSG;

            // Register to internet connection observer
            if (!mIsInternetObserving) {
                RegisterNetworkObserver();
            }


            //Log.i("sharonlog","SERVICE STARTED");
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

    public static int getServiceStatus() {

        // Getter of service status

        return mServiceStatus;
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

        if (progressCode == SYNC_ERROR) {

            mSystemSession.setInSync(null, false);
            cancelNotification(ObserverService.SYNC_NOTIFICATION);
            showSyncDetailsNotification("Sync error :(", android.R.drawable.stat_notify_error);
        } else if (progressCode == SYNC_DONE) {
            cancelNotification(ObserverService.SYNC_NOTIFICATION);
            showSyncDetailsNotification("Sync complete", android.R.drawable.stat_sys_download_done);
        } else if (progressCode == SYNC_PAUSE) {
            cancelNotification(ObserverService.SYNC_NOTIFICATION);

        }

        sendResult(MESSAGE_FROM_SERVICE_PROGRESS, progressCode, message);
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

            if (mSystemSession.getInSync(null)) {
                sendProgress(SYNC_PAUSE);
            }
        } else {
            // Internet is connected
            Log.e("sharonlog","Internet connection changed : " +  internetStatus);

            // Making sure using wifi if user selected it
            if ( !mSettingsSession.getWIFIOnly() ||
                    (internetStatus != ConnectivityManager.TYPE_MOBILE && mSettingsSession.getWIFIOnly())) {
                if (mContactsObserver != null) {
                    mContactsObserver.Manage();
                }

                if (mPhotosObserver != null) {
                    mPhotosObserver.Manage();
                }

                // TODO
                sendProgress(SYNC_START);
            }
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


    // Sync notification
    public void showSyncNotification() {


        Notification noti = new Notification.Builder(this)
                .setContentTitle("KeepItSafe")
                .setContentText("Sync is in process")
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .build();


        //noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(SYNC_NOTIFICATION, noti);
    }



    public void cancelNotification(int id) {

        mNotificationManager.cancel(id);
    }


    public void showSyncDetailsNotification(String message, int res) {

        cancelNotification(DETAILS_NOTIFICATION);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Notification noti = new Notification.Builder(this)
                .setContentTitle("KeepItSafe")
                .setContentText(message)
                .setSmallIcon(res)
                .setSound(alarmSound)
                .build();


        //noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(DETAILS_NOTIFICATION, noti);
    }

}
