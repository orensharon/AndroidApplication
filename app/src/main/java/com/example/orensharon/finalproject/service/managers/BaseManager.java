package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.db.ContentBL;
import com.example.orensharon.finalproject.service.db.DBContent;
import com.example.orensharon.finalproject.service.helpers.QueryArgs;
import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.service.objects.Contact.MyContact;
import com.example.orensharon.finalproject.service.objects.Photo.MyPhoto;
import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.Connectivity;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by orensharon on 12/17/14.
 * This class is the base of the manager module
 */
public abstract class BaseManager {


    private Uri mObservingUri;

    // Make public for derivative classes usage
    public Context mContext;

    private ContentBL mContentBL;
    private ContentSession mContentSession;
    private SettingsSession mSettingsSession;
    private SystemSession mSystemSession;

    // The upload manager of the contents
    private UploadManager mUploadManager;

    protected String mContentType;


    private ObserverService mServiceInstance;

    public BaseManager(Context context, Uri uri, String contentType) {

        mContext = context;
        mObservingUri = uri;
        mContentSession = new ContentSession(mContext);
        mSettingsSession = new SettingsSession(mContext);
        mSystemSession = new SystemSession(mContext);

        mContentType = contentType;
        // Init the upload manager component
        mUploadManager = new UploadManager();

        mContentBL = new ContentBL(mContext);

        mServiceInstance =
                (ObserverService)mContext;


        //mSystemSession.setInSync(mContentType, false);
        Log.e("sharontest",mContentType + " BaseManager Construector: [" + mSystemSession.getInSync(null) +
                "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");


        //mContentBL.setInSync(513,false,"Photo");
        //mSystemSession.setInSync(null,false);
        Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());

    }






    public BaseObject HandleContent() {

        // Will be called from the observers. This method checks if new content arrived
        // Or existing content was edited or deleted

        BaseObject result = null;

        // Checking if the trigger occurs due to new content arrival
        if (isNewContent()) {

            // A new content

            // Get the content
            result = getLastContent();

            // Save the latest id of the local storage content
            mContentSession.setLatestId(mContentType, getLatestSystemID());

            // Save meta data of the content to database
            mContentBL.InsertContent(result);

            Log.i("sharonlog",mContentType + " new latest id:" + mContentSession.getLatestId(mContentType));

        } else {

            // Content might be deleted or edited content

            Log.i("sharonlog",mContentType + " Checking if was edited...");

            // Checking if the content was edited
            ArrayList<BaseObject> list = getAllContents();

            // TODO: not here
            for (BaseObject content : list) {

                int key = content.getId();

                DBContent dbContent = mContentBL.isContentExist(mContentType, content.getId());
                if (dbContent != null) {

                    Log.i("sharonlog",mContentType + " Checking content with id " + dbContent.getId() );
                    Log.i("sharonlog",mContentType + " Before checksum " + dbContent.getChecksum());
                    Log.i("sharonlog",mContentType + " After checksum " + content.getChecksum());

                    // Comparing checksum of the contents
                    if (!dbContent.getChecksum().equals(content.getChecksum())) {

                        // Content was edited - set new checksum, update flags

                        Log.e("sharonlog",mContentType + " EDITED!");

                        result = getContentByID(key);
                        mContentBL.setChecksum(content.getId(), content.getChecksum(),mContentType);
                        mContentBL.setSynced(content.getId(), false, mContentType);
                        mContentBL.setDirty(content.getId(), true, mContentType);
                        mContentBL.setDateModified(content.getId(), System.currentTimeMillis(), mContentType);

                    }
                }

            }


        }

        return result;
    }


    // Handling with syncing
    public void Manage() {

        // Managing the existing image of the content.
        // Method is checking the greatest id number and according that assuming was will
        // be a new content. after managing it calls Sync to upload data to safe


        // Make sure syncing is not in process
        if (!mSystemSession.getInSync(null)) {


                Log.e("sharonlog", mContentType + " Now managing: " + mObservingUri.toString());
                Log.e("sharonlog", mContentType + " Before Managing ID: " + mContentSession.getLatestId(mContentType));

                // Get and save the latest system id of the content table
                int latestSystemID = getLatestSystemID();
                int savedSystemID = mContentSession.getLatestId(mContentType);

                if (latestSystemID > savedSystemID) {
                    // Means the saved ID is not synced with the actual one
                    mContentSession.setLatestId(mContentType, latestSystemID);
                }

                Log.e("sharonlog", mContentType + " After Managing ID: " + mContentSession.getLatestId(mContentType));
                Log.i("sharonlog", mContentType + " Content List:");
                Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());

                // TODO: check for edits
                if (mServiceInstance.getServiceStatus() == ObserverService.STATUS_SERVICE_RUNNING) {
                    //Sync();
                }


            }
    }

    public void Sync()
    {

        // After managing, the content need to be send to the safe.
        // This function check all terms to start sync using HandleUnsyncedContent()

        // Ignore sending if wifi only and the connection is not wifi
        boolean isWifiOnly = mSettingsSession.getWIFIOnly();
        boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);

        if (Connectivity.isConnected(mContext) && (isWifiOnly && connectedToWifi) || !isWifiOnly) {


            Log.e("sharonlog", mContentType + " All terms ok, Calling HandleUnsyncedContent()");

            // TODO: Thread
            HandleUnsyncedContent();
        } else {
            // Means that need to pause the sync

            mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);

            Log.e("sharontest",mContentType + " Sync no internet: [" + mSystemSession.getInSync(null) +
                    "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");
        }


    }

    public void HandleUnsyncedContent() {

        // This method is handling with the unsynced content
        // It will seek (from the local image of db) the next content to upload
        // After getting the next content it will upload it using DispatchRequest method

        Log.e("sharonlog",mContentType + " Syncing unsynced list...");

        // Getting next content to upload
        DBContent next = mContentBL.getNextInSync(mContentType);

        // Check if there is next content to upload
        if (next != null) {


            // Setting in sync flag
            mSystemSession.setInSync(mContentType, true);
            mSystemSession.setInSync(null,true);

            Log.e("sharontest",mContentType + " Sync: [" + mSystemSession.getInSync(null) +
                    "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");


            // Get the next content object by id
            BaseObject content = getContentByID(next.getId());

            // Check if the content exist
            if (content == null) {

                // Means content was deleted from local device
                Log.e("sharonlog", mContentType + " Cant find content.. deleting " + next.getId());

                // Delete the not existing content and get the next one
                mContentBL.DeleteContent(mContentType, next.getId());

                // Recursive call to get the next content
                HandleUnsyncedContent();
            }
            else {

                // There is a next content object
                Log.e("sharonlog",mContentType + " Dispaching ..." + content.getId());
                Log.i("sharonlog",mContentType + " Content List:");
                Log.i("sharonlog",mContentBL.getAllContents(mContentType).toString());

                // show sync notification
                mServiceInstance.showSyncNotification();

                // Dispatch the content to upload it to safe
                mUploadManager.DispatchRequest(content, true);
            }
        } else {

            // Means there is not further contents to upload

            boolean nothingToDo = true;

            // If nothingToDo == false means the sync is done
            if (mSystemSession.getInSync(null) == true) {
                nothingToDo = false;
            }
            mSystemSession.setInSync(mContentType, false);

            Log.e("sharontest",mContentType + " Finish: [" + mSystemSession.getInSync(null) +
                    "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");

            // Sync complete OK
            if (!mSystemSession.getInSync("Photo") && !mSystemSession.getInSync("Contact"))  {
                mSystemSession.setInSync(null, false);

                // Sync is done!
                Log.e("sharontest",mContentType + " Finish All: [" + mSystemSession.getInSync(null) +
                        "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");

                if (!nothingToDo) {
                    mServiceInstance.sendProgress(ObserverService.SYNC_DONE);
                }
            }
            Log.e("sharonlog",mContentType + " nothing to sync");
        }

    }


    public int getLatestSystemID() {

        // Returns the last id of the stored content of

        QueryArgs queryArgs;
        String sortingOrder;
        String[] projection = new String[]{ContactsContract.Contacts._ID};
        Cursor cursor;

        int result = 0;

        sortingOrder = "_ID DESC LIMIT 1";
        queryArgs = new QueryArgs(mObservingUri,projection, null, null, sortingOrder);

        // Getting a cursor according the required query
        cursor = getCursor(queryArgs);

        if (cursor != null) {

            // Make sure the cursor is not null before iterate
            if (cursor.moveToNext()) {
                // Iterate over the cursor, creating new contact and add it to the list

                result = Integer.parseInt(getColumnString(cursor, "_ID"));
            }
            cursor.close();
        }
        return result;
    }

    private boolean isNewContent() {

        // Returns true of the greatest id of the content db is higher then the stored one
        // If does - assuming this is a new content

        int latestIDFromDB;

        latestIDFromDB = mContentBL.getLastIDByContentType(mContentType);

        return latestIDFromDB < getLatestSystemID();

    }

    private BaseObject getContentByID(int id) {

        // Returns a base object from local storage by given id

        QueryArgs queryArgs;

        String selection;
        String[] selectionArgs;
        Cursor cursor;
        BaseObject result = null;

        selection = "_ID = ?";
        selectionArgs = new String[] { String.valueOf(id) };

        queryArgs = new QueryArgs(mObservingUri,null, selection, selectionArgs, null);

        cursor = getCursor(queryArgs);

        if (cursor != null) {

            // Make sure the cursor is not null before iterate
            if (cursor.moveToNext()) {
                // Iterate over the cursor, creating new contact and add it to the list

                result = getContent(cursor);
            }
            cursor.close();
        }

        return result;
    }

    private BaseObject getLastContent() {

        // Get the last content stored in local db

        QueryArgs queryArgs;
        String sortingOrder;
        Cursor cursor;
        BaseObject result = null;



        sortingOrder = "_id desc limit 1";
        queryArgs = new QueryArgs(mObservingUri,null, null, null, sortingOrder);

        cursor = getCursor(queryArgs);

        if (cursor != null) {

            // Make sure the cursor is not null before iterate
            if (cursor.moveToNext()) {
                // Iterate over the cursor, creating new contact and add it to the list

                result = getContent(cursor);
            }
            cursor.close();
        }

        return result;
    }

    private ArrayList<BaseObject> getAllContents() {

        // Get all contents from local db

        QueryArgs queryArgs;
        Cursor cursor;
        BaseObject result;
        ArrayList<BaseObject> list;
        // If there is more then one content - latestContents will be a list
        // Otherwise it will be a one BaseObject

        list = new ArrayList<BaseObject>();

        queryArgs = new QueryArgs(mObservingUri,null, null, null, null);

        cursor = getCursor(queryArgs);

        if (cursor != null) {

            // Make sure the cursor is not null before iterate
            while (cursor.moveToNext()) {
                // Iterate over the cursor, creating new contact and add it to the list

                result = getBaseContent(cursor);
                list.add(result);
            }
            cursor.close();
        }

        return list;
    }



    // TODO singleton
    public UploadManager getUploadManager() {
        return mUploadManager;
    }

    public void CancelSyncing() {
        mUploadManager.CancelSyncing();
    }



    // Helper for the children classes
    protected String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    // Get a cursor from a given query args
    public Cursor getCursor(QueryArgs queryArgs) {

        // From given query parameters and a destination Uri - The method will
        // run a query and return a cursor to the matched rows

        Cursor cursor;

        cursor = mContext.getContentResolver().query(
                queryArgs.getUri(),
                queryArgs.getProjection(),
                queryArgs.getSelection(),
                queryArgs.getSelectionArgs(),
                queryArgs.getSortOrder());

        if (cursor == null) {
            // Cursor error
            return null;
        }

        if (cursor.getCount() == 0) {

            // No records found - we can close the connection and return null
            cursor.close();
            return null;
        }

        return cursor;
    }

    // Getter to the observing Uri
    public Uri getObservingUri() {
        return mObservingUri;
    }



    // Will called in each child class
    public abstract BaseObject getContent(Cursor cursor);

    public abstract BaseObject getBaseContent(Cursor cursor);



    public class UploadManager {




        private RequestFactory mRequestFactory;

        public UploadManager() {

            // Upload manager constructor
            mRequestFactory = new RequestFactory(mContext);
        }


        public void DispatchRequest(BaseObject newContent, boolean syncing) {

            // Getting a new content and a syncing flag
            // If syncing flag == true means in a middle of a sync (calling the next content after done upload)
            // Method will dispatch the content to the request pool and then to safe

            String typeOfContent;


            typeOfContent = newContent.getTypeOfContent();

            // Ignore sending if wifi only and the connection is not wifi
            boolean isWifiOnly = mSettingsSession.getWIFIOnly();
            boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);

            // Before dispatching - check if the conditions of the network meet user pref.
            if (Connectivity.isConnected(mContext) && (isWifiOnly && connectedToWifi) || !isWifiOnly) {

                // Meet user pref.


                // Check what type of content (contact or photo)
                if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {

                    // Photo to upload
                    Log.e("sharonlog", mContentType + " All terms ok, Calling UploadPhoto()");

                    final MyPhoto myPhoto = (MyPhoto) newContent;
                    UploadPhoto(myPhoto, syncing, 1);


                } else if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {

                    // Contact to upload
                    Log.e("sharonlog", mContentType + " All terms ok, Calling UploadContact()");
                    final MyContact myContact = (MyContact) newContent;

                    UploadContact(myContact, syncing, 1);
                }

            } else {
                // There is not internet connectivity in the middle of the sync
                // Cancel the sync

               // mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);

                //Log.e("sharontest",mContentType + " Sync no internet: [" + mSystemSession.getInSync(null) +
                  //      "," + mSystemSession.getInSync("Photo") + "," + mSystemSession.getInSync("Contact") + "]");
            }

        }

        public void CancelSyncing() {

            // Canceling all queued requests of the specific mContentType type


            Log.e("sharonlog", mContentType + " Canceling " + mContentType);

            mRequestFactory.CancelByTag(mContentType);
            mContentBL.CancelAllInSync(mContentType);
;

        }

        public void CancelAllSyncing() {

            // Canceling all queued requests of the specific mContentType type


            Log.e("sharonlog", mContentType + " Canceling all....");

            mRequestFactory.CancelAll();
            mContentBL.CancelAllInSync(mContentType);


        }


        private void UploadContact(final BaseObject baseObject, final boolean syncing, final int ipRequestCount) {

            // Upload a contact into the safe.
            // From a given baseObject, syncing flag to know if in a middle of syncing
            // And ip request counter in case of safe unreachable case
            // Build a request and upload it to server

            JSONObject body;

            final MyContact myContact = (MyContact)baseObject;
            body = myContact.toJSONObject();

            // Building the upload API
            String ip = mSystemSession.geIPAddressOfSafe();
            String apiSuffix;

            if (mContentBL.getDirty(myContact.getId(), mContentType)) {
                apiSuffix = ApplicationConstants.CONTACT_UPDATE_API_SUFFIX;
            } else {
                apiSuffix = ApplicationConstants.CONTACT_INSERT_API_SUFFIX;
            }
            String url = mContext.getString(R.string.http_prefix) + ip + apiSuffix;

            // Adding parameters into the body
            try {

                // Adding content type to body
                body.put(ApplicationConstants.CONTENT_TYPE_OF_CONTENT_KEY, myContact.getTypeOfContent());

                // Adding time stamps to body
                DBContent dbContent =  mContentBL.getById(baseObject.getId(), mContentType);
                body.put(ApplicationConstants.CONTENT_CREATED_TIME_STAMP_KEY,
                        dbContent.getDateCreated());
                body.put(ApplicationConstants.CONTENT_MODIFIED_TIME_STAMP_KEY,
                        dbContent.getDateModified());

                // Adding the MAC address of this device
                body.put(ApplicationConstants.DEVICE_PHYSICAL_ADDRESS_TOKEN, getMACAddress());

            } catch (JSONException e) {
                body = null;
            }

            // Make sure the json object was successfully created
            if (body != null) {

                mContentBL.setInSync(myContact.getId(), true, myContact.getTypeOfContent());
                Log.i("sharonlog", mContentType + " Content List:");
                Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());


                mRequestFactory.createJsonRequest(
                        Request.Method.POST,
                        url,
                        mContentType,
                        body.toString(),
                        mSystemSession.getToken(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                // Means the content was successfully uploaded
                                mContentBL.setInSync(myContact.getId(), false, mContentType);
                                mContentBL.setSynced(myContact.getId(), true, mContentType);
                                mContentBL.setDirty(myContact.getId(), false, mContentType);

                                Log.i(mContentType + " sharonlog", myContact.getId() + " Done!\nAfter sending.:");
                                Log.i(mContentType + " sharonlog", mContentBL.getAllContents(mContentType).toString());

                                //Toast.makeText(mContext, myContact.getId() + " - Done!",
                                //        Toast.LENGTH_LONG).show();
                                if (syncing) {
                                    // If a middle of sync - get the next content to sync using HandleUnsyncedContent
                                    Log.i("sharonlog", mContentType + " Next....");
                                    HandleUnsyncedContent();
                                }


                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                String errorMessage = null;

                                Log.i(mContentType + " sharonlog", "ERROR!");

                                // Canceling the inSync flag
                                mContentBL.setInSync(myContact.getId(), false, mContentType);

                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    switch (response.statusCode) {

                                        // 400
                                        case ApplicationConstants.HTTP_BAD_REQUEST:
                                            errorMessage = mContext.getString(R.string.http_bad_request);
                                            break;

                                        // 403
                                        case ApplicationConstants.HTTP_FORBIDDEN:
                                            errorMessage = mContext.getString(R.string.http_forbidden);
                                            break;

                                        // 405
                                        case ApplicationConstants.HTTP_METHOD_NOT_ALLOWED:
                                            errorMessage = mContext.getString(R.string.http_method_not_allowed);
                                            break;

                                        // 409
                                        case ApplicationConstants.HTTP_CONFLICT:
                                            errorMessage = mContext.getString(R.string.http_conflict);
                                            break;

                                        // 500
                                        case ApplicationConstants.HTTP_INTERNAL_SERVER_ERROR:
                                            errorMessage = mContext.getString(R.string.http_internal_server_error);
                                            break;


                                    }

                                    //if (response.statusCode != ApplicationConstants.HTTP_METHOD_NOT_ALLOWED) {
                                        // Rising the error flag of the spec. content
                                        mContentBL.setReturnedError(myContact.getId(), true, mContentType);

                                        if (syncing) {
                                            // If a middle of sync - get the next content to sync using HandleUnsyncedContent
                                            Log.i(mContentType + " sharonlog", "but Next....");
                                            HandleUnsyncedContent();
                                        }
                                    //}

                                } else if (error.getMessage() != null) {
                                    errorMessage = error.getMessage();
                                }


                                if (errorMessage != null) {

                                    if (errorMessage.contains("unreachable")) {

                                        // Safe is unreachable, cancel all the queued requests
                                        CancelAllSyncing();

                                        Log.i(mContentType + " sharonlog", "cant find safe.... retries:" + ipRequestCount);

                                        // Cant find safe
                                        if (ipRequestCount > 0) {
                                            // Try to get the ip address of safe again - it may have been changed
                                            RequestSafeIP(myContact, syncing);
                                        } else {

                                            mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);

                                        }


                                    } else if (errorMessage.contains("connectivity")) {

                                        // Some issue with the network connectivity
                                        CancelAllSyncing();
                                        mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);

                                    }
                                }
                            }
                        }
                );
            }
        }

        private void UploadPhoto(final BaseObject baseObject, final boolean syncing, final int ipRequestRetriesCount) {

            // Upload a photo into the safe.
            // From a given baseObject, syncing flag to know if in a middle of syncing
            // And ip request counter in case of safe unreachable case
            // Build a request and upload it to server


            final MyPhoto myPhoto = (MyPhoto) baseObject;

            // Building upload API
            String ip = mSystemSession.geIPAddressOfSafe();
            String url = mContext.getString(R.string.http_prefix) + ip + ApplicationConstants.PHOTO_INSERT_API_SUFFIX;

            JSONObject body = new JSONObject();
            try {

                // Adding content type to body
                body.put(ApplicationConstants.CONTENT_TYPE_OF_CONTENT_KEY, myPhoto.getTypeOfContent());

                // Adding time stamps to body
                DBContent dbContent =  mContentBL.getById(baseObject.getId(), mContentType);
                body.put(ApplicationConstants.CONTENT_CREATED_TIME_STAMP_KEY,
                        dbContent.getDateCreated());
                body.put(ApplicationConstants.CONTENT_MODIFIED_TIME_STAMP_KEY,
                        dbContent.getDateModified());

                // Adding the MAC address of this device
                body.put(ApplicationConstants.DEVICE_PHYSICAL_ADDRESS_TOKEN, getMACAddress());

                // Adding ID of content to the body
                body.put(ApplicationConstants.CONTENT_ID_KEY, myPhoto.getId());
            } catch (JSONException e) {
                body = null;
            }

            // Make sure the json object was successfully created
            if (body != null) {


                mContentBL.setInSync(myPhoto.getId(), true, myPhoto.getTypeOfContent());
                Log.i("sharonlog", mContentType + " Content List:");
                Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());


                // Creating the photo upload request
                mRequestFactory.createMultipartRequest(
                        url,
                        myPhoto.getTypeOfContent(),
                        myPhoto.getFile(),
                        body,
                        mSystemSession.getToken(),
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {


                                // Means the content was successfully uploaded
                                mContentBL.setInSync(myPhoto.getId(), false, mContentType);
                                mContentBL.setSynced(myPhoto.getId(), true, mContentType);
                                mContentBL.setDirty(myPhoto.getId(), false, mContentType);

                                Log.i("sharonlog", mContentType + " " + myPhoto.getId() + " Done!\nAfter sending.:");
                                Log.i("sharonlog", mContentType + " " + mContentBL.getAllContents(mContentType).toString());


                                if (syncing) {
                                    // If a middle of sync - get the next content to sync using HandleUnsyncedContent
                                    Log.i("sharonlog", mContentType + " Next....");
                                    HandleUnsyncedContent();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                String errorMessage = null;

                                Log.i("sharonlog", mContentType + " ERROR!");

                                // Canceling inSync flag
                                mContentBL.setInSync(myPhoto.getId(), false, mContentType);

                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    switch (response.statusCode) {

                                        // 400
                                        case ApplicationConstants.HTTP_BAD_REQUEST:
                                            errorMessage = mContext.getString(R.string.http_bad_request);
                                            break;

                                        // 403
                                        case ApplicationConstants.HTTP_FORBIDDEN:
                                            errorMessage = mContext.getString(R.string.http_forbidden);
                                            break;

                                        // 405
                                        case ApplicationConstants.HTTP_METHOD_NOT_ALLOWED:
                                            errorMessage = mContext.getString(R.string.http_method_not_allowed);
                                            break;

                                        // 409
                                        case ApplicationConstants.HTTP_CONFLICT:
                                            errorMessage = mContext.getString(R.string.http_conflict);
                                            break;

                                        // 500
                                        case ApplicationConstants.HTTP_INTERNAL_SERVER_ERROR:
                                            errorMessage = mContext.getString(R.string.http_internal_server_error);
                                            break;


                                    }

                                    //if (response.statusCode != ApplicationConstants.HTTP_METHOD_NOT_ALLOWED) {
                                        // Rising the error flag of the spec. content
                                        mContentBL.setReturnedError(myPhoto.getId(), true, mContentType);


                                        if (syncing) {
                                            // If a middle of sync - get the next content to sync using HandleUnsyncedContent
                                            Log.i("sharonlog", mContentType + " but Next....");
                                            HandleUnsyncedContent();
                                        }
                                    //}

                                } else if (error.getMessage() != null) {
                                    errorMessage = error.getMessage();

                                }
                                Log.i("sharonlog", errorMessage);
                                if (errorMessage != null) {

                                    if (errorMessage.contains("unreachable")) {

                                        // Safe is unreachable, cancel all the queued requests
                                        CancelAllSyncing();
                                        Log.i("sharonlog", mContentType + " cant find safe.... retries:" + ipRequestRetriesCount);


                                        if (ipRequestRetriesCount > 0) {
                                            // Try to get the ip address of safe again - it may have been changed
                                            RequestSafeIP(myPhoto, syncing);
                                        } else {

                                            mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);
                                        }

                                    } else if (errorMessage.contains("connectivity")) {

                                        // Network connectivity issue, cancel all the queued requests
                                        CancelAllSyncing();
                                        mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);

                                    }

                                }
                            }
                        });
            }
        }


        private void RequestSafeIP(final BaseObject content, final boolean syncing) {

            // Create a request to server to get the ip address of the safe
            // Given parameters will be used on recovery mode

            RequestFactory requestFactory = new RequestFactory(mContext);
            JSONObject body = new JSONObject();

            Log.i("sharonlog", mContentType + " ip request ....");

            requestFactory.createJsonRequest(
                    Request.Method.POST,
                    ApplicationConstants.IP_GET_API,
                    ApplicationConstants.IP_REQUEST_TAG,
                    body.toString(),
                    mSystemSession.getToken(),

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String ip = null;

                            // Extract the safe IP from the response
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                ip = jsonObject.getString(ApplicationConstants.IP_GETTER_IP_ADDRESS_KEY);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // null for error reading from json
                            // Check if this is a valid ip address
                            IPAddressValidator ipAddressValidator;
                            ipAddressValidator = new IPAddressValidator();

                            if (ip != null && ipAddressValidator.validate(ip)) {

                                // Save only if new ip
                                if (!ip.equals(mSystemSession.geIPAddressOfSafe())) {
                                    mSystemSession.setIPAddressOfSafe(ip);
                                }

                                //Toast.makeText(mContext, mSystemSession.geIPAddressOfSafe(),
                                //        Toast.LENGTH_LONG).show();
                                Log.i("sharonlog", mContentType + " got ip:" + ip);


                                // Resend the content but with no more retries
                                if (content.getTypeOfContent().equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {
                                    UploadPhoto(content,syncing, 0);
                                } else if (content.getTypeOfContent().equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {
                                    UploadContact(content, syncing, 0);
                                }


                            } else {

                                // Getting issues from get the ip of the safe
                                mSystemSession.setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);

                            }

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = null;

                            // Canceling inSync flag
                            mContentBL.setInSync(content.getId(), false, mContentType);

                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                switch (response.statusCode) {

                                    // 400
                                    case ApplicationConstants.HTTP_BAD_REQUEST:
                                        errorMessage = mContext.getString(R.string.http_bad_request);
                                        break;

                                    // 403
                                    case ApplicationConstants.HTTP_FORBIDDEN:
                                        errorMessage = mContext.getString(R.string.http_forbidden);
                                        break;

                                    // 409
                                    case ApplicationConstants.HTTP_CONFLICT:
                                        errorMessage = mContext.getString(R.string.http_conflict);
                                        break;

                                    // 500
                                    case ApplicationConstants.HTTP_INTERNAL_SERVER_ERROR:
                                        errorMessage = mContext.getString(R.string.http_internal_server_error);
                                        break;


                                }

                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();
                            }

                            if (errorMessage != null) {
                                
                            }

                            CancelAllSyncing();
                            mServiceInstance.sendProgress(ObserverService.SYNC_ERROR);
                        }
                    });

        }

        private String getMACAddress() {

            // Get the local MAC address, use this for each content upload

            WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            return info.getMacAddress();

        }

    }


}
