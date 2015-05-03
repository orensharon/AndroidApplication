package com.example.orensharon.finalproject.service;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.example.orensharon.finalproject.service.upload.UploadManager;
import com.example.orensharon.finalproject.service.observers.BaseContentObserver;
import com.example.orensharon.finalproject.service.observers.ContactObserver;
import com.example.orensharon.finalproject.service.observers.PhotoObserver;


/**
 * Created by orensharon on 11/30/14.
 * This is implantation of a background service.
 * Service job is to register to a content observers according to the
 * user's selection.
 */
public class ObserverService extends Service {

    // Constants
    public static final int STATUS_SERVICE_NOT_RUNNING = 0;
    public static final int STATUS_SERVICE_RUNNING = 1;
    public static final int STATUS_SERVICE_ERROR = 2;

    // Broadcasting communication constants
    public static final String MSG_TO_SERVICE = "to_service_msg";
    public static final String MSG_FROM_SERVICE = "from_service_msg";

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

        sendResult(SERVICE_NOT_RUNNING_MSG);

        // Unregister from services
        this.getApplicationContext().getContentResolver()
                .unregisterContentObserver(mPhotosObserver);


        this.getApplicationContext().getContentResolver()
                .unregisterContentObserver(mContactsObserver);

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


            // Init the concrete observers
            mContactsObserver = new ContactObserver(this, CONTACT_OBSERVER_URI);
            mPhotosObserver = new PhotoObserver(this, PHOTO_OBSERVER_URI);

            RegisterToObserver(CONTACT_OBSERVER_URI, mContactsObserver);
            RegisterToObserver(PHOTO_OBSERVER_URI, mPhotosObserver);



            Log.d("INSTANT", "registered content observer");
        } else if (mServiceStatus == STATUS_SERVICE_RUNNING) {

            // If service is already running - manage the content
            message = "Settings saved on service";
            //mContactsObserver.Manage();
            //mPhotosObserver.Manage();
        }


        // Set the status anyway
        mServiceStatus = STATUS_SERVICE_RUNNING;
        sendResult(message);




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

    private void sendResult(String message) {

        // Send a broadcast to the loader activity

        Intent intent;

        intent = new Intent(ObserverService.class.getName());
        if (message != null) {
            intent.putExtra(MSG_FROM_SERVICE, message);
        }

        // Send the broadcast message
        mBroadcaster.sendBroadcast(intent);
    }

    public static int getServiceStatus() {

        // Getter of service status

        return mServiceStatus;
    }


}
