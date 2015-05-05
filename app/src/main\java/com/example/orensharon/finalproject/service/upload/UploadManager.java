package com.example.orensharon.finalproject.service.upload;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.logic.RequestFactory;

import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.service.objects.Contact.MyContact;
import com.example.orensharon.finalproject.service.objects.Photo.MyPhoto;
import com.example.orensharon.finalproject.service.upload.helpers.SyncUpdateMessage;

import com.example.orensharon.finalproject.service.upload.helpers.NetworkChangeReceiver;
import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.Connectivity;
import com.example.orensharon.finalproject.utils.MD5Checksum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by orensharon on 12/30/14.
 */
public class UploadManager implements Observer {


    private Context mContext;
    private RequestFactory mRequestFactory;
    private SystemSession mSystemSession;
    private ContentSession mContentSession;
    private SettingsSession mSettingsSession;

    private ApplicationConstants.ContentKeys mContentKeys;

    public UploadManager(Context context, ApplicationConstants.ContentKeys contentKeys) {

        mContext = context;
        mRequestFactory = new RequestFactory(mContext);
        mSystemSession = new SystemSession(mContext);
        mSettingsSession = new SettingsSession(mContext);
        mContentSession = new ContentSession(mContext);
        mContentKeys = contentKeys;
        // Register to the network observer.
        // The main job of this observer is once the internet connection is online
        // To sync the unsynced items

        //RegisterNetworkObserver();
       // NetworkChangeReceiver.UpdateInternetStatus(mContext);
    }


    public void RegisterNetworkObserver() {

        // Register to the internet connection observer
        NetworkChangeReceiver.getObservable().addObserver(this);
    }
    public void UnregisterNetworkObserver() {
        // Unregister to the internet connection observer

        Log.i("Unregister NetworkObserver","");
        NetworkChangeReceiver.getObservable().deleteObserver(this);
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







    public void DispatchRequest(BaseObject newContent) {

        String typeOfContent;
        String ip, url;

        typeOfContent = newContent.getTypeOfContent();
        ip = mSystemSession.geIPAddressOfSafe();

        // Ignore sending if wifi only and the connection is not wifi
        boolean isWifiOnly = mSettingsSession.getWIFIOnly();
        boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);

        if ((isWifiOnly && connectedToWifi) || !isWifiOnly) {
            if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {

                url = "http://" + ip + ApplicationConstants.PHOTO_UPLOAD_STREAM_API_SUFFIX;

                final MyPhoto myPhoto = (MyPhoto)newContent;
                UploadPhoto(url, myPhoto);


            } else if(typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {
                url = "http://" + ip + ApplicationConstants.CONTACT_UPLOAD_API_SUFFIX;


                final MyContact myContact = (MyContact)newContent;

                UploadContact(url, myContact);
            }

        }



    }




    private void UploadContact(String url, final MyContact myContact) {
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


                            // TODO: replace the condition
                            if (response != null) {

                                // Means the content was successfully uploaded
                                mContentSession.RemoveFromUnsyncedList(
                                        mContentKeys.getUnsyncedListKey(),
                                        myContact.getId()
                                );

                            } else {
                                // Unexpected response

                                Toast.makeText(mContext, "Unexpected response (Not error)",
                                        Toast.LENGTH_LONG).show();
                            }

                            Log.i("sharonlog","Unsyncned list: (after sending)");
                            Log.i("sharonlog",mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

                            Log.i("sharonlog","toBackup list: (after sending)");
                            Log.i("sharonlog",mContentSession.getToBackupList(mContentKeys.getBackupDataListKey()).toString());
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
                    }
            );
        }
    }

    private void UploadPhoto(String url, final MyPhoto myPhoto) {

        mRequestFactory.createMultipartRequest(
                url,
                myPhoto.getTypeOfContent(),
                myPhoto.getFile(),
                myPhoto.getId(),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        // Comparing the uploaded MD5 hash from response
                        // With the one we just have sent

                        String uploadResult;
                        JSONObject jsonResponse = (JSONObject) response;

                        try {
                            //TODO: read from headers
                            uploadResult = jsonResponse.getString(ApplicationConstants.CONTENT_STREAM_UPLOAD_RESULT_KEY);
                            uploadResult = uploadResult.replace("\"","");

                            String localFileHash = MD5Checksum.getMd5Hash(myPhoto.getFile());

                            Toast.makeText(mContext, localFileHash,
                                    Toast.LENGTH_LONG).show();

                            if (localFileHash.equals(uploadResult)) {
                                // Means the content was successfully uploaded
                                mContentSession.RemoveFromUnsyncedList(
                                        mContentKeys.getUnsyncedListKey(),
                                        myPhoto.getId()
                                );

                            } else {
                                // TODO: the content didn't sent correctly
                            }
                            Log.i("sharonlog","Unsyncned list: (after sending)");
                            Log.i("sharonlog",mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

                            Log.i("sharonlog","toBackup list: (after sending)");
                            Log.i("sharonlog",mContentSession.getToBackupList(mContentKeys.getBackupDataListKey()).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // TODO: the content didn't sent correctly
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error != null)
                            Log.d("photo error", String.valueOf(error.getCause()));
                    }
                }) ;
    }
}
