package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.service.helpers.QueryArgs;
import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.service.observers.InternetObserver;
import com.example.orensharon.finalproject.service.upload.UploadManager;
import com.example.orensharon.finalproject.service.upload.helpers.NetworkChangeReceiver;
import com.example.orensharon.finalproject.service.upload.helpers.SyncUpdateMessage;
import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.Connectivity;
import com.example.orensharon.finalproject.utils.MD5Checksum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by orensharon on 12/17/14.
 * This class is the base of the manager module
 */
public abstract class BaseManager {

    private static final String APP_NAME = "KEEP_IT_APP";
    public final String CLASS_NAME = this.getClass().getSimpleName();
    private final String KEY_LAST_ID = CLASS_NAME + ".LAST_ID";
    private final String KEY_ID_DOES_NOT_EXIST = "0";
    private ApplicationConstants.ContentKeys mContentKeys;

    private Uri mObservingUri;

    // Make public for derivative classes usage
    public Context mContext;

    private ContentSession mContentSession;
    private SettingsSession mSettingsSession;

    // TODO: should be static?
    // The upload manager of the contents
    private UploadManager mUploadManager;

    public BaseManager(Context context, Uri uri, ApplicationConstants.ContentKeys contentKeys) {

        mContext = context;
        mObservingUri = uri;
        mContentSession = new ContentSession(mContext);
        mSettingsSession = new SettingsSession(mContext);
        mContentKeys = contentKeys;

        // Init the upload manager component
        mUploadManager = new UploadManager(mContext, mContentKeys);


    }


    public void Manage() {

        // Get and save the latest system id of the content table
        Log.e("sharonlog","Now managing: " + mObservingUri.toString());
        Log.e("sharonlog","Before Managing ID: " + mContentSession.getLatestId(mContentKeys.getLastIDKey()));

        int latestSystemID = getLatestSystemID();
        int savedSystemID = mContentSession.getLatestId(mContentKeys.getLastIDKey());

        if (latestSystemID > savedSystemID) {
            // Means the saved ID is not synced with the actual one
            mContentSession.setLatestId(mContentKeys.getLastIDKey(),latestSystemID);
        }
        Log.e("sharonlog","After Managing ID: " + mContentSession.getLatestId(mContentKeys.getLastIDKey()));

        if ( mSettingsSession.getAutoSync() ) {

            Log.e("sharonlog","Is in AUTOSYNC mode");
            // Ignore sending if wifi only and the connection is not wifi
            boolean isWifiOnly = mSettingsSession.getWIFIOnly();
            boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);

            if (Connectivity.isConnected(mContext) && (isWifiOnly && connectedToWifi) || !isWifiOnly) {
                // TODO: Where to put this?
                Log.e("sharonlog","All terms ok, Calling HandleUnsyncedContent()");
                HandleUnsyncedContent();
            }
        }


    }

    // TODO: thread
    public void HandleUnsyncedContent() {
        List<String> unsyncedList;

        Log.i("sharonlog","Unsyncned list:");
        Log.i("sharonlog",mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

        Log.i("sharonlog","Backuped list:");
        Log.i("sharonlog",mContentSession.getToBackupList(mContentKeys.getBackupDataListKey()).toString());

        Log.e("sharonlog","Syncing unsynced list...");
        unsyncedList = mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey());
        for (String key : unsyncedList) {
            BaseObject content = getContentByID(key);
            Log.e("sharonlog","Calling ... DispatchRequest(content)");
            mUploadManager.DispatchRequest(content);
        }
    }

    public UploadManager getUploadManager() {
        return mUploadManager;
    }

    private int getLatestSystemID() {

        // Returns the last id of the stored content

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

        if (mContentSession.getLatestId(mContentKeys.getLastIDKey()) < getLatestSystemID()){
            // Means this a new content
            return true;
        }

        return false;
    }

    private BaseObject getContentByID(String id) {
        QueryArgs queryArgs;

        String selection;
        String[] selectionArgs;
        Cursor cursor;
        BaseObject result = null;

        selection = "_ID = ?";
        selectionArgs = new String[] { id };

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

    public BaseObject HandleContent() {

        BaseObject result = null;


        if (isNewContent() == true) {

            // A new content

            String checksum;

            // New content - save the latest id
            mContentSession.setLatestId(mContentKeys.getLastIDKey(), getLatestSystemID());

            result = getLastContent();
            checksum = result.getChecksum();

            // Update my copy of the synced items
            mContentSession.AddBackupedDataList(mContentKeys.getBackupDataListKey(), result.getId(), checksum);

            // Putting the request into the unsynced list
            // Upon success response - the request will be removed from the list
            mContentSession.AddToUnsyncedList(mContentKeys.getUnsyncedListKey(), result.getId());

            Log.i("sharonlog","Unsyncned list:");
            Log.i("sharonlog",mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

            Log.i("sharonlog","Backup list:");
            Log.i("sharonlog",mContentSession.getToBackupList(mContentKeys.getBackupDataListKey()).toString());


        } else {

            // Might be deleted or edited content

            Log.i("sharonlog","Checking if was edited...");
            ArrayList<BaseObject> list = getAllContents();

            Map<String,String> data = mContentSession.getToBackupList(mContentKeys.getBackupDataListKey());
            for (BaseObject content : list) {

                String key = String.valueOf(content.getId());
                if (data.containsKey(key)) {
                    Log.i("sharonlog","Checking content with id " + content.getId());
                    Log.i("sharonlog","Before checksum " + data.get(key));
                    Log.i("sharonlog","After checksum " + content.getChecksum());
                    if (!data.get(key).equals(content.getChecksum())) {
                        Log.e("sharonlog","EDITED!");
                        //data.put(key, content.getChecksum());

                        result = getContentByID(key);
                        mContentSession.UpdateContentFromData(mContentKeys.getBackupDataListKey(), key, content.getChecksum());

                        // Putting the request into the unsynced list
                        // Upon success response - the request will be removed from the list
                        mContentSession.AddToUnsyncedList(mContentKeys.getUnsyncedListKey(), key);

                        // TODO:
                        //break;
                    }
                }

            }
            Log.e("sharonlog", "Backuped list (after editing):");
            Log.e("sharonlog", mContentSession.getToBackupList(mContentKeys.getBackupDataListKey()).toString());

            Log.i("sharonlog","Unsyncned list (after editing):");
            Log.i("sharonlog",mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

        }
        return result;
    }

    private ArrayList<BaseObject> getAllContents() {

        QueryArgs queryArgs;
        Cursor cursor;
        BaseObject result = null;
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





    public String get() {

        String result = null;

        //String sortingOrder = "_id desc limit 1";

        QueryArgs queryArgs = new QueryArgs(mObservingUri,null, null, null, null);

        // Taking the first element of the list - we limited the size to 1 anyway
        Cursor cursor = getCursor(queryArgs);

        if (cursor != null) {

            Log.e("sharonlog","Device Content:");
            // Make sure the cursor is not null before iterate
            while (cursor.moveToNext()) {
                // Iterate over the cursor, creating new contact and add it to the list

                result = getContacts(cursor);
                Log.e("sharonlog",result.toString());
            }
            cursor.close();
            return result;
        }

        // Now latest content - may be a fresh new database
        return null;

    }

    public String getContent1(Cursor cursor) {

        // Reading from device content database according to the given uri
        if (cursor != null) {

            String filePath, mimeType, title, id;
            String dateAdded, dateModified;

            id = getColumnString(cursor, MediaStore.MediaColumns._ID);
            filePath = getColumnString(cursor, MediaStore.MediaColumns.DATA);
            mimeType = getColumnString(cursor, MediaStore.MediaColumns.MIME_TYPE);
            title = getColumnString(cursor, MediaStore.MediaColumns.TITLE);
            dateAdded = getColumnString(cursor, MediaStore.MediaColumns.DATE_ADDED);
            dateModified = getColumnString(cursor, MediaStore.MediaColumns.DATE_MODIFIED);

            String result = "{_ID:" + id + ", Checksum: " + MD5Checksum.getMd5HashFromFilePath(filePath) + "}";

            return result;
        }

        return null;
    }

    public String getContacts(Cursor cursor) {
        String id, name, version = null;

        if (cursor != null) {
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //version = getVersion(id);

            String result = "{_ID:" + id + " name: " + name + " version: " + version;

            return result;
        }
        return null;

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


}
