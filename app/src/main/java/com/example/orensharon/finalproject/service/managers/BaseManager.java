package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
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

    // The upload manager of the contents
    private UploadManager mUploadManager;

    protected String mContentType;



    public BaseManager(Context context, Uri uri, String contentType) {

        mContext = context;
        mObservingUri = uri;
        mContentSession = new ContentSession(mContext);
        mSettingsSession = new SettingsSession(mContext);

        mContentType = contentType;
        // Init the upload manager component
        mUploadManager = new UploadManager();

        mContentBL = new ContentBL(mContext);

    }






    // Will be called from the observers
    public BaseObject HandleContent() {

        BaseObject result = null;


        if (isNewContent() == true) {

            // A new content
            result = getLastContent();


            // New content - save to database
            mContentBL.InsertContent(result);

            // New content - save the latest id
            mContentSession.setLatestId(mContentType, getLatestSystemID());
            Log.i("sharonlog","new latest id:" + mContentSession.getLatestId(mContentType));

        } else {

            // Might be deleted or edited content

            Log.i("sharonlog","Checking if was edited...");

            ArrayList<BaseObject> list = getAllContents();

            for (BaseObject content : list) {

                int key = content.getId();

                DBContent dbContent = mContentBL.isContentExist(mContentType, content.getId());
                if (dbContent != null) {

                    Log.i("sharonlog","Checking content with id " + dbContent.getId() );
                    Log.i("sharonlog","Before checksum " + dbContent.getChecksum());
                    Log.i("sharonlog","After checksum " + content.getChecksum());
                    if (!dbContent.getChecksum().equals(content.getChecksum())) {
                        Log.e("sharonlog","EDITED!");

                        result = getContentByID(key);

                        mContentBL.setChecksum(content.getId(), content.getChecksum(),mContentType);
                        mContentBL.setSynced(content.getId(), false, mContentType);

                        break;
                    }
                }

            }


        }

        return result;
    }


    // Handling with syncing
    public void Manage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get and save the latest system id of the content table
                Log.e("sharonlog","Now managing: " + mObservingUri.toString());
                Log.e("sharonlog","Before Managing ID: " + mContentSession.getLatestId(mContentType));

                int latestSystemID = getLatestSystemID();
                int savedSystemID = mContentSession.getLatestId(mContentType);

                if (latestSystemID > savedSystemID) {
                    // Means the saved ID is not synced with the actual one
                    mContentSession.setLatestId(mContentType, latestSystemID);
                }
                Log.e("sharonlog","After Managing ID: " + mContentSession.getLatestId(mContentType));

                Log.i("sharonlog","Content List:");
                Log.i("sharonlog",mContentBL.getAllContents(mContentType).toString());

                // TODO: check for edits

                // Ignore sending if wifi only and the connection is not wifi
                boolean isWifiOnly = mSettingsSession.getWIFIOnly();
                boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);

                if (Connectivity.isConnected(mContext) && (isWifiOnly && connectedToWifi) || !isWifiOnly) {

                    Log.e("sharonlog","All terms ok, Calling HandleUnsyncedContent()");
                    HandleUnsyncedContent();
                }


            }
        }).start();

    }

    public void HandleUnsyncedContent() {

        Log.e("sharonlog","Syncing unsynced list...");


        DBContent next = mContentBL.getNextInSync(mContentType);

        if (next != null) {
            BaseObject content = getContentByID(next.getId());

            if (content == null) {
                // Means content was deleted from local device
                Log.e("sharonlog", "Cant find content.. deleting " + next.getId());

                // TODO:
                mContentBL.DeleteContent(mContentType, next.getId());
                HandleUnsyncedContent();
            }
            else {
                Log.e("sharonlog","Dispaching ..." + content.getId());

                Log.i("sharonlog","Content List:");
                Log.i("sharonlog",mContentBL.getAllContents(mContentType).toString());
                mUploadManager.DispatchRequest(content, true);
            }
        } else {
            Log.e("sharonlog","nothing to sync");
            if (mContentBL.getAllUnsyncedContents(null).size() == 0) {
//                Toast.makeText(mContext, "Sync done",
  //                      Toast.LENGTH_LONG).show();
            }
        }






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

        int latestIDFromDB;

        latestIDFromDB = mContentBL.getLastIDByContentType(mContentType);

        if (latestIDFromDB < getLatestSystemID()){
            // Means this a new content
            return true;
        }

        return false;
    }

    private BaseObject getContentByID(int id) {
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



    // TODO singleton
    public UploadManager getUploadManager() {
        return mUploadManager;
    }

    public void CancelSyncing() {
        mUploadManager.CancelSyncing();
    }


    private void RequestSafeIP() {

        // Create a request to server to get the ip address of the safe

        final SystemSession systemSession = new SystemSession(mContext);


        RequestFactory requestFactory = new RequestFactory(mContext);
        JSONObject body = new JSONObject();


        requestFactory.createJsonRequest(
                Request.Method.GET,
                ApplicationConstants.IP_GET_API, body.toString(), systemSession.getToken(),
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
                            if (!ip.equals(systemSession.geIPAddressOfSafe())) {
                                systemSession.setIPAddressOfSafe(ip);
                            }

                            Toast.makeText(mContext, systemSession.geIPAddressOfSafe(),
                                    Toast.LENGTH_LONG).show();
                            Log.i("sharonlog", "got ip:" + ip);


                            Log.i("sharonlog", "Calling UploadPhoto(..)");


                        } else {
                            //TODO:// RequestSafeIP(token); with retry policy
                            systemSession.setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);
                            Toast.makeText(mContext, "NO-IP",
                                    Toast.LENGTH_LONG).show();

                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = null;

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            switch (response.statusCode) {

                                // 400
                                case ApplicationConstants.HTTP_BAD_REQUEST:
                                    errorMessage = "400 Bad Request";
                                    break;

                                // 403
                                case ApplicationConstants.HTTP_FORBIDDEN:
                                    errorMessage = "Forbidden attempt to upload";
                                    break;

                                // 409
                                case ApplicationConstants.HTTP_CONFLICT:
                                    errorMessage = "MD5 not equal";
                                    break;


                            }

                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }


                        if (errorMessage != null) {
                            Toast.makeText(mContext, errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

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

        private SystemSession mSystemSession;
        private RequestFactory mRequestFactory;

        public UploadManager() {


            mRequestFactory = new RequestFactory(mContext);
            mSystemSession = new SystemSession(mContext);

        }


        public void DispatchRequest(BaseObject newContent, boolean syncing) {

            String typeOfContent;
            String ip, url;

            typeOfContent = newContent.getTypeOfContent();
            ip = mSystemSession.geIPAddressOfSafe();

            // Ignore sending if wifi only and the connection is not wifi
            boolean isWifiOnly = mSettingsSession.getWIFIOnly();
            boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);


            if (Connectivity.isConnected(mContext) && (isWifiOnly && connectedToWifi) || !isWifiOnly) {


                mContentBL.setInSync(newContent.getId(), true, newContent.getTypeOfContent());

                Log.i("sharonlog", "Content List:");
                Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());

                if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {

                    Log.e("sharonlog", "All terms ok, Calling UploadPhoto()");

                    url = "http://" + ip + ApplicationConstants.PHOTO_UPLOAD_STREAM_API_SUFFIX;

                    final MyPhoto myPhoto = (MyPhoto) newContent;
                    UploadPhoto(url, myPhoto, syncing);


                } else if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {
                    url = "http://" + ip + ApplicationConstants.CONTACT_UPLOAD_API_SUFFIX;

                    Log.e("sharonlog", "All terms ok, Calling UploadContact()");
                    final MyContact myContact = (MyContact) newContent;

                    UploadContact(url, myContact, syncing);
                }

            }

        }

        public void CancelSyncing() {
            mRequestFactory.Suspend();
            Log.e("sharonlog", "Canceling all....");
            mContentBL.CancelAllInSync(mContentType);
            Log.i("sharonlog", "Content List:");
            Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());
        }

        private void UploadContact(String url, final MyContact myContact, final boolean syncing) {
            JSONObject body;
            body = myContact.toJSONObject();

            // Adding the token into the body of the message
            try {
                //body.put(ApplicationConstants.AUTH_TOKEN_KEY,mSystemSession.getToken());
                body.put(ApplicationConstants.CONTENT_TYPE_OF_CONTENT_KEY, ApplicationConstants.TYPE_OF_CONTENT_CONTACT);
            } catch (JSONException e) {
                body = null;
            }

            // Make sure the json object was successfully created
            if (body != null) {

                mRequestFactory.createJsonRequest(
                        Request.Method.POST,
                        url,
                        body.toString(),
                        mSystemSession.getToken(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                // Means the content was successfully uploaded
                                mContentBL.setInSync(myContact.getId(), false, mContentType);
                                mContentBL.setSynced(myContact.getId(), true, mContentType);
                                //   ObserverService serviceInstance =
                                //           (ObserverService)mContext;
                                //   serviceInstance.sendProgress(ObserverService.SYNC_DONE);


                                Log.i("sharonlog", myContact.getId() + " Done!\nAfter sending.:");
                                Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());

                                if (syncing) {
                                    Log.i("sharonlog", "Next....");
                                    HandleUnsyncedContent();
                                }


                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                String errorMessage = null;

                                Log.i("sharonlog", "ERROR!");

                                mContentBL.setInSync(myContact.getId(), false, mContentType);
                                mContentBL.setReturnedError(myContact.getId(), true, mContentType);

                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    switch (response.statusCode) {

                                        // 400
                                        case ApplicationConstants.HTTP_BAD_REQUEST:
                                            errorMessage = "400 Bad Request";
                                            break;

                                        // 403
                                        case ApplicationConstants.HTTP_FORBIDDEN:
                                            errorMessage = "Forbidden attempt to upload";
                                            break;

                                        // 409
                                        case ApplicationConstants.HTTP_CONFLICT:
                                            errorMessage = "MD5 not equal";
                                            break;


                                    }

                                } else if (error.getMessage() != null) {
                                    errorMessage = error.getMessage();
                                }


                                if (errorMessage != null) {

                                    if (errorMessage.contains("unreachable")) {

                                        CancelSyncing();

                                        ObserverService serviceInstance =
                                                (ObserverService) mContext;
                                        serviceInstance.sendError(ObserverService.SAFE_UNREACHABLE);
                                    } else if (errorMessage.contains("connectivity")) {

                                        CancelSyncing();

                                        ObserverService serviceInstance =
                                                (ObserverService) mContext;
                                        serviceInstance.sendError(ObserverService.NO_INTERNET);
                                    }

                                    Toast.makeText(mContext, errorMessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
            }
        }

        private void UploadPhoto(final String url, final BaseObject baseObject, final boolean syncing) {

            final MyPhoto myPhoto = (MyPhoto) baseObject;

            mRequestFactory.createMultipartRequest(
                    url,
                    myPhoto.getTypeOfContent(),
                    myPhoto.getFile(),
                    myPhoto.getId(),
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {

                            // Means the content was successfully uploaded
                            mContentBL.setInSync(myPhoto.getId(), false, mContentType);
                            mContentBL.setSynced(myPhoto.getId(), true, mContentType);
                            //   ObserverService serviceInstance =
                            //           (ObserverService)mContext;
                            //   serviceInstance.sendProgress(ObserverService.SYNC_DONE);


                            Log.i("sharonlog", myPhoto.getId() + " Done!\nAfter sending.:");
                            Log.i("sharonlog", mContentBL.getAllContents(mContentType).toString());

                            if (syncing) {
                                Log.i("sharonlog", "Next....");
                                HandleUnsyncedContent();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String errorMessage = null;

                            Log.i("sharonlog", "ERROR!");

                            mContentBL.setInSync(myPhoto.getId(), false, mContentType);
                            mContentBL.setReturnedError(myPhoto.getId(), true, mContentType);

                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                switch (response.statusCode) {

                                    // 400
                                    case ApplicationConstants.HTTP_BAD_REQUEST:
                                        errorMessage = "400 Bad Request";
                                        break;

                                    // 403
                                    case ApplicationConstants.HTTP_FORBIDDEN:
                                        errorMessage = "Forbidden attempt to upload";
                                        break;

                                    // 409
                                    case ApplicationConstants.HTTP_CONFLICT:
                                        errorMessage = "MD5 not equal";
                                        break;


                                }

                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();

                            }

                            if (errorMessage != null) {

                                if (errorMessage.contains("unreachable")) {

                                    CancelSyncing();

                                    ObserverService serviceInstance =
                                            (ObserverService) mContext;
                                    serviceInstance.sendError(ObserverService.SAFE_UNREACHABLE);
                                } else if (errorMessage.contains("connectivity")) {

                                    CancelSyncing();

                                    ObserverService serviceInstance =
                                            (ObserverService) mContext;
                                    serviceInstance.sendError(ObserverService.NO_INTERNET);
                                }

                                Toast.makeText(mContext, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    });

        }

    }
}
