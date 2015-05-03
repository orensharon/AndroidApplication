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
import com.example.orensharon.finalproject.service.upload.UploadManager;
import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.utils.MD5Checksum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    // The upload manager of the contents
    private UploadManager mUploadManager;

    public BaseManager(Context context, Uri uri, ApplicationConstants.ContentKeys contentKeys) {

        mContext = context;
        mObservingUri = uri;
        mContentSession = new ContentSession(mContext);
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


        // TODO: Where to put this?
        HandleUnsyncedContent();

        Log.e("sharonlog","After Managing ID: " + mContentSession.getLatestId(mContentKeys.getLastIDKey()));

    }

    private void HandleUnsyncedContent() {
        List<String> unsyncedList;

        Log.i("sharonlog","Unsyncned list:");
        Log.i("sharonlog",mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

        Log.i("sharonlog","Backup list:");
        Log.i("sharonlog",mContentSession.getToBackupList(mContentKeys.getBackupDataListKey()).toString());


        unsyncedList = mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey());
        for (String key : unsyncedList) {
            BaseObject content = getContentByID(key);
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













    // Manage the reference of the content from database
    // Means - saving the last id of the most new content (by id)
  /*
    public void Manage() {

        // Mange method job is to monitor and keep an reference of the synced contents
        // according to the id column - we will check what is the most last id from the device database
        // vs the saved last id of the content table

        String databaseLatestId;

        //String savedLatestId;

        // Get the last saved id from the shared preferences
        mLastSavedId = getLastSavedContentId();

        // Save a pointer to the most latest content

        mLatestContent = requestLatestContentFromDatabase();
        databaseLatestId = getIdOfContent(mLatestContent);

        Log.w("START ---" + CLASS_NAME + "---", "Manage()");
        Log.w("Before, Last current DB id: " + databaseLatestId, "Last current saved id: " + mLastSavedId);

        //if (mLatestContent != null)
        //    Log.e("mLatestContent",mLatestContent.toString());


        // Third - check if there is some old content to sync
        handleUnsyncedContents(databaseLatestId);
        setLastSavedContentId(databaseLatestId);

        Log.w("After, Now current DB id: " + databaseLatestId, "Now current saved id: " + mLastSavedId);
        Log.w("END ---" + CLASS_NAME + "---", "Manage()");
    }

    // Checker if the content is new one using the difference in the value id of
    public BaseObject getNewContent() {

        boolean b;
        String dbId;
        BaseObject tempNewestContent;

        tempNewestContent = requestLatestContentFromDatabase();
        // Update the pointer to the most latest content and get the id of it
        dbId = getIdOfContent(tempNewestContent);


        //if (mLatestContent != null)
        //    Log.e("mLatestContent",mLatestContent.toString());
        // Checking if the id of the most latest content is greater then the saved one
        b = Long.valueOf(dbId) > Long.valueOf(mLastSavedId);

        // Save the new most latest id in local storage

        // According to the previous condition - return the object or null
        if (b) {

            Log.w(CLASS_NAME + ".getNewContent() --START-- change in db, latest is: " + mLastSavedId + " and db id is:", dbId);


            mLatestContent = tempNewestContent;
            setLastSavedContentId(dbId);
            Log.w(CLASS_NAME + ".getNewContent() --START-- change in db, new latest is: " + mLastSavedId + " and db id is:", dbId);
            return mLatestContent;
        }

        return null;
    }





    // Shared Preferences Helpers
    private String getLastSavedContentId() {
        return mSharedPreferences.getString(KEY_LAST_ID, KEY_ID_DOES_NOT_EXIST);
    }

    // Set new value for id in Shared Preferences
    private void setLastSavedContentId(String databaseLastId) {

        // Update the new id to the shared preferences

        SharedPreferences.Editor editor;
        editor = mSharedPreferences.edit();
        editor.putString(KEY_LAST_ID, databaseLastId);
        editor.apply();

        mLastSavedId = databaseLastId;
        // Fifth - Update the lastId flag
        //mLastId = databaseLastId;
    }

    // By given content object - returns the id of it
    private String getIdOfContent(Object object) {

        return  ((object==null) ? KEY_ID_DOES_NOT_EXIST : BaseObject.class.cast(object).getId());

    }

    // Return object type of the latest added content according to the observer uri
    public BaseObject requestLatestContentFromDatabase() {

        // Returns a generic type of a the last content in the database
        // sorting by the id column desc

        // Create a list of unsynced contents

        QueryArgs queryArgs;
        String sortingOrder;

        // If there is more then one content - latestContents will be a list
        // Otherwise it will be a one BaseObject
        Object latestContents;


        sortingOrder = "_id desc limit 1";

        queryArgs = new QueryArgs(mObservingUri,null, null, null, sortingOrder);

        // Taking the first element of the list - we limited the size to 1 anyway
        latestContents = requestContents(queryArgs);

        // Make sure the latest content is really exist
        if (latestContents != null) {
            return ((List<BaseObject>)latestContents).get(0);
        }

        // Now latest content - may be a fresh new database
        return null;
    }

    // Return a list of Object type of all the unsynced contents
    private List<BaseObject> requestListOfUnsyncedContents() {

        // Create a list of unsynced contents

        QueryArgs queryArgs;
        String selection;
        String[] selectionArgs;
        List<BaseObject> unsyncedContents;

        selection = BaseColumns._ID + " > ? ";
        selectionArgs = new String[]{ mLastSavedId };

        queryArgs = new QueryArgs(mObservingUri,null, selection, selectionArgs, null);
        unsyncedContents = requestContents(queryArgs);

        return unsyncedContents;
    }

    // Getting the last id of the database and handling all the unsynced contant
    private void handleUnsyncedContents(String databaseLastId) {

        // Check if the given database id is greater then saved one.
        // If does - need to sync the difference contents and the shared preferences id

        List<BaseObject> unsyncedContents;

        unsyncedContents = null;
        if ( Long.valueOf(mLastSavedId) < Long.valueOf(databaseLastId)) {

            // Create a list of unsynced contents
            unsyncedContents = requestListOfUnsyncedContents();
            Log.e("to sync:",unsyncedContents.toString());

            // Send the unsynced items to the request pool
            for (BaseObject content : unsyncedContents) {
                ObserverService.getUploadManager().DispatchRequest(content);
            }


        }


    }

    // Return a list of Object type of contents according to the query arg
    private List<BaseObject> requestContents(QueryArgs queryArgs) {


        BaseObject content;
        List<BaseObject> contentList;

        Cursor cursor;

        contentList = new ArrayList<BaseObject>();

        cursor = getCursor(queryArgs);

        if (cursor != null) {
            // Make sure the cursor is not null before iterate

            while (cursor.moveToNext()) {
                // Iterate over the cursor, creating new contact and add it to the list

                content = getContent(cursor);
                //Log.i("requestContents, content",content.toString());
                contentList.add(content);
            }
            cursor.close();
            return contentList;
        }

        return null;

    }
*/
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
