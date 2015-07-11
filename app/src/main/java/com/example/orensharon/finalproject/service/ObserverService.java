package com.example.orensharon.finalproject.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.service.db.ContentBL;
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
    public static final int SYNC_READY = 24;

    // Internal error code
    public static final int SAFE_UNREACHABLE = 40;
    public static final int NO_INTERNET = 30;



    // Service status constants
    private final String SERVICE_RUNNING_MSG = "Service is running";
    private final String SERVICE_NOT_RUNNING_MSG = "Service is not running";

    // Observers Uri constants
    private static final Uri PHOTO_OBSERVER_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final Uri CONTACT_OBSERVER_URI = ContactsContract.Contacts.CONTENT_URI;

    // Binder given to clients
    private final IBinder mBinder = new ServiceBinder();

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
    private static boolean mFirstStart = true;

    private NotificationManager mNotificationManager;

    public final static int SYNC_NOTIFICATION = 10002;
    public final static int DETAILS_NOTIFICATION = 10003;

    @Override
    public void onCreate() {

        // Serivce creation

        super.onCreate();

        // To send messages to activity
        mBroadcaster = LocalBroadcastManager.getInstance(this);

        // For the notifications
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);


        // Init the sessions
        mSystemSession = new SystemSession(this);
        mSettingsSession = new SettingsSession(getApplicationContext());

        Log.e("sharontest","Service OnCreate: [" + mSystemSession.getInSync(null) +
        "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");

        handleRecover();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Service was started
        String message = null;


        super.onStartCommand(intent, flags, startId);

        // If the service is started at the first time - send message its running
        if (mServiceStatus == STATUS_SERVICE_NOT_RUNNING) {
            mServiceStatus = STATUS_SERVICE_RUNNING;
            message = SERVICE_RUNNING_MSG;

            // Register to internet connection observer
            if (!mIsInternetObserving) {
                RegisterNetworkObserver();
            }
        }

        // Set the status anyway
        mServiceStatus = STATUS_SERVICE_RUNNING;

        StartObservers();


        Log.e("sharontest","Service before fix: [" + mSystemSession.getInSync(null) +
                "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");


        // Fix the notification flag status if not in a middle of sync
        if (!mSystemSession.getInSync("Photo") && !mSystemSession.getInSync("Contact"))  {
            mSystemSession.setInSync(null, false);
        }


        Log.e("sharontest","Service check if can remove noti: [" + mSystemSession.getInSync(null) +
                "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");

        // Send message to activity
        sendResult(2, STATUS_SERVICE_RUNNING, message);


        // For auto start after crash
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        // Stop the service by user or by the OS

        super.onDestroy();

        // Sending service status to the activity
        mServiceStatus = STATUS_SERVICE_NOT_RUNNING;
        sendResult(2, STATUS_SERVICE_NOT_RUNNING, SERVICE_NOT_RUNNING_MSG);


        // Unregister from observers
        StopObservers();

        // Wait for sync to show notification
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (mSystemSession.getInSync(null));
                cancelNotification(SYNC_NOTIFICATION);
                mSystemSession.setInSync(null,false);
            }
        }).start();


        // Unregister from internet connection observer
        if (mIsInternetObserving) {
            UnregisterNetworkObserver();
        }

        Log.i("sharonlog","Service destroyed");


    }





    // Rolling back the states of the 'middle-of-sync' flags
    private void handleRecover() {


        ContentBL contentBL = new ContentBL(this);


        // If created after crash
        // Cancel notification after service was crashed during process
        if (mFirstStart) {

            Log.e("sharonlog", "First creation - cancel sync");

            contentBL.CancelAllInSync(ApplicationConstants.TYPE_OF_CONTENT_PHOTO);
            contentBL.CancelAllInSync(ApplicationConstants.TYPE_OF_CONTENT_CONTACT);
            mSystemSession.setInSync(null, false);
            cancelNotification(SYNC_NOTIFICATION);
            mFirstStart = false;
        }
    }



    // Start the observers
    private void StartObservers() {


        if( mContactsObserver == null) {
            mContactsObserver = new ContactObserver(this, CONTACT_OBSERVER_URI);
        }

        if (mPhotosObserver == null) {
            mPhotosObserver = new PhotoObserver(this, PHOTO_OBSERVER_URI);
        }


        StartObserver(ApplicationConstants.TYPE_OF_CONTENT_CONTACT);
        StartObserver(ApplicationConstants.TYPE_OF_CONTENT_PHOTO);

    }

    // Stop the observers
    private void StopObservers() {
        StopObserver(ApplicationConstants.TYPE_OF_CONTENT_CONTACT);
        StopObserver(ApplicationConstants.TYPE_OF_CONTENT_PHOTO);
    }


    // Start given observer
    public void StartObserver(String type) {

        if (mSettingsSession.getUserContentItem(type)) {

            if (type.equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {
                RegisterToObserver(CONTACT_OBSERVER_URI, mContactsObserver);
            } else if (type.equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {
                RegisterToObserver(PHOTO_OBSERVER_URI, mPhotosObserver);
            }

            Log.i("sharonlog", "register " + type);
        }
    }

    // Stop given observer
    public void StopObserver(String type) {

        if (type.equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {
            UnregisterFromObserver(mContactsObserver);
        } else if (type.equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {
            UnregisterFromObserver(mPhotosObserver);
        }

        Log.i("sharonlog", "unregister " + type);

    }





    // Register a given observer according to the given uri
    private void RegisterToObserver(Uri uri, BaseContentObserver observer) {



        this.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        uri, false,
                        observer);
    }

    // Unregister from given observer
    private void UnregisterFromObserver(BaseContentObserver contentObserver) {

        // Also canceling existing request

        if (contentObserver != null) {

            Log.e("sharonlog","UnregisterFromObserver ");

            this.getApplicationContext().getContentResolver()
                    .unregisterContentObserver(contentObserver);

        }
    }




    // Getter of service status
    public static int getServiceStatus() {

        return mServiceStatus;
    }

    // Start sync the contents
    public void Sync() {

        sendProgress(SYNC_START);
        mPhotosObserver.Sync();
        mContactsObserver.Sync();
    }




    // User recevier send messages to activity user sends functions
    private void sendResult(int resultType, int code, String message) {

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
    public void sendProgress(int progressCode) {

        String message = "";

        if (progressCode == SYNC_ERROR) {

            if (mSystemSession.getInSync(null)) {
                cancelNotification(ObserverService.SYNC_NOTIFICATION);
                showSyncDetailsNotification(getString(R.string.sync_error_message), android.R.drawable.stat_notify_error);
            }

            mSystemSession.setInSync(null, false);
        } else if (progressCode == SYNC_DONE) {
            cancelNotification(ObserverService.SYNC_NOTIFICATION);
            showSyncDetailsNotification(getString(R.string.sync_done_messsage), android.R.drawable.stat_sys_download_done);
        } else if (progressCode == SYNC_PAUSE) {
            cancelNotification(ObserverService.SYNC_NOTIFICATION);

        }

        sendResult(MESSAGE_FROM_SERVICE_PROGRESS, progressCode, message);
    }






    // Network observer
    public void RegisterNetworkObserver() {

        // Register to the internet connection observer
        NetworkChangeReceiver.getObservable().addObserver(this);
        mIsInternetObserving = true;
        Log.i("sharonlog", "Registered to internet observing");
    }
    public void UnregisterNetworkObserver() {
        // Unregister to the internet connection observer

        Log.i("sharonlog", "Unregistered from internet observing");
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
                sendProgress(SYNC_ERROR);
            }
        } else {
            // Internet is connected
            Log.e("sharonlog","Internet connection changed : " +  internetStatus);

            // TODO: remove
            // Making sure using wifi if user selected it
           /* if ( !mSettingsSession.getWIFIOnly() ||
                    (internetStatus != ConnectivityManager.TYPE_MOBILE && mSettingsSession.getWIFIOnly())) {
                if (mContactsObserver != null) {
                    mContactsObserver.Manage();
                }

                if (mPhotosObserver != null) {
                    mPhotosObserver.Manage();
                }

            }*/
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



    // Sync notifications
    public void showSyncNotification() {


        Notification noti = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.sync_in_proccess_notification))
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
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(res)
                .setSound(alarmSound)
                .build();


        //noti.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(DETAILS_NOTIFICATION, noti);
    }



    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class ServiceBinder extends Binder {
        public ObserverService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ObserverService.this;
        }
    }



    public void Evil() {
        Log.e("sharonlog","Killing 0_0");
        int x = 1 / 0;
    }
}
