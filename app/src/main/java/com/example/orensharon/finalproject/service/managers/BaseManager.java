package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.orensharon.finalproject.MainActivityTemp;
import com.example.orensharon.finalproject.service.helpers.QueryArgs;
import com.example.orensharon.finalproject.service.objects.BaseObject;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by orensharon on 12/17/14.
 * This class is the base of the manager module
 */
public abstract class BaseManager {

    public final String CLASS_NAME = this.getClass().getSimpleName();
    private final String KEY_LAST_ID = CLASS_NAME + ".LAST_ID";
    private final String KEY_ID_DOES_NOT_EXIST = "0";


    // Make private for internal usage
    private Object mLatestContent;
    private String mLastSavedId;

    private Uri mObservingUri;

    // Make public for derivative classes usage
    public Context mContext;
    public SharedPreferences mSharedPreferences;



    public BaseManager(Context context, Uri uri) {

        mContext = context;
        mObservingUri = uri;
        mSharedPreferences = mContext.getSharedPreferences(MainActivityTemp.APP_NAME, Context.MODE_PRIVATE);
    }


    // Manage the reference of the content from database
    // Means - saving the last id of the most new content (by id)
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
    public Object getNewContent() {

        boolean b;
        String dbId;
        Object tempNewestContent;

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
    private Object requestLatestContentFromDatabase() {

        // Returns a generic type of a the last content in the database
        // sorting by the id column desc

        // Create a list of unsynced contents

        QueryArgs queryArgs;
        String sortingOrder;

        Object latestContent;


        sortingOrder = "_id desc limit 1";

        queryArgs = new QueryArgs(mObservingUri,null, null, null, sortingOrder);

        // Taking the first element of the list - we limited the size to 1 anyway
        latestContent = requestContents(queryArgs);

        // Make sure the latest content is really exist
        if (latestContent != null) {
            return ((List<Object>)latestContent).get(0);
        }

        // Now latest content - may be a fresh new database
        return null;
    }

    // Return a list of Object type of all the unsynced contents
    private List<Object> requestListOfUnsyncedContents() {

        // Create a list of unsynced contents

        QueryArgs queryArgs;
        String selection;
        String[] selectionArgs;
        List<Object> unsyncedContents;

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

        List<Object> unsyncedContents;

        unsyncedContents = null;
        if ( Long.valueOf(mLastSavedId) < Long.valueOf(databaseLastId)) {

            // Create a list of unsynced contents
            unsyncedContents = requestListOfUnsyncedContents();
            Log.e("to sync:",unsyncedContents.toString());
            // TODO: add to pool

        }


    }

    // Return a list of T type of contents according to the query arg
    private List<Object> requestContents(QueryArgs queryArgs) {


        Object content;
        List<Object> contentList;

        Cursor cursor;

        contentList = new ArrayList<Object>();

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



    // Helper for the children classes
    protected String getColumnString(Cursor cursor, String columnName) {


        return cursor.getString(cursor.getColumnIndex(columnName));

    }


    // Will called in each child class
    public abstract Object getContent(Cursor cursor);


}
