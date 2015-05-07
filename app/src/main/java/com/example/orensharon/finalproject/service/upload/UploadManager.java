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

import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.service.objects.Contact.MyContact;
import com.example.orensharon.finalproject.service.objects.Photo.MyPhoto;

import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.Connectivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by orensharon on 12/30/14.
 */
public class UploadManager {


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







    public void DispatchRequest(BaseObject newContent, boolean isSyncing) {

        String typeOfContent;
        String ip, url;

        typeOfContent = newContent.getTypeOfContent();
        ip = mSystemSession.geIPAddressOfSafe();

        // Ignore sending if wifi only and the connection is not wifi
        boolean isWifiOnly = mSettingsSession.getWIFIOnly();
        boolean connectedToWifi = Connectivity.isConnectedWifi(mContext);


        if (Connectivity.isConnected(mContext) && (isWifiOnly && connectedToWifi) || !isWifiOnly) {


            if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_PHOTO)) {

                Log.e("sharonlog", "All terms ok, Calling UploadPhoto()");

                url = "http://" + ip + ApplicationConstants.PHOTO_UPLOAD_STREAM_API_SUFFIX;

                final MyPhoto myPhoto = (MyPhoto) newContent;
                UploadPhoto(url, myPhoto, isSyncing);


            } else if (typeOfContent.equals(ApplicationConstants.TYPE_OF_CONTENT_CONTACT)) {
                url = "http://" + ip + ApplicationConstants.CONTACT_UPLOAD_API_SUFFIX;

                Log.e("sharonlog", "All terms ok, Calling UploadContact()");
                final MyContact myContact = (MyContact) newContent;

                UploadContact(url, myContact);
            }

        }

    }

    public void Suspend() {
        mRequestFactory.Suspend();
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

                                mRequestFactory.Suspend();
                                Log.i("sharonlog", "ERROR!");

                                Toast.makeText(mContext, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );
        }
    }

    private void UploadPhoto(final String url, final BaseObject baseObject, final boolean isSyncing) {

            final MyPhoto myPhoto = (MyPhoto)baseObject;

            mRequestFactory.createMultipartRequest(
                    url,
                    myPhoto.getTypeOfContent(),
                    myPhoto.getFile(),
                    myPhoto.getId(),
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {


                            // Check if done with syncing
                            if (isSyncing == true) {
                                List<String> list = mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey());

                                // Checking if working on the last item in list
                                // Then it means done syncing...
                                if (list.get(list.size() - 1) == baseObject.getId()) {
                                    Toast.makeText(mContext, "Done Syncing",
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            // Means the content was successfully uploaded
                            mContentSession.RemoveFromUnsyncedList(
                                    mContentKeys.getUnsyncedListKey(),
                                    myPhoto.getId()
                            );
                             //   ObserverService serviceInstance =
                             //           (ObserverService)mContext;
                             //   serviceInstance.sendProgress(ObserverService.SYNC_DONE);


                            Log.i("sharonlog", "Unsyncned list: (after sending)");
                            Log.i("sharonlog", mContentSession.getUnsyncedList(mContentKeys.getUnsyncedListKey()).toString());

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String errorMessage = null;

                            Log.i("sharonlog", "ERROR!");




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

                                    mRequestFactory.Suspend();

                                    ObserverService serviceInstance =
                                            (ObserverService)mContext;
                                    serviceInstance.sendError(ObserverService.SAFE_UNREACHABLE);
                                } else if (errorMessage.contains("connectivity")) {

                                    mRequestFactory.Suspend();

                                    ObserverService serviceInstance =
                                            (ObserverService)mContext;
                                    serviceInstance.sendError(ObserverService.NO_INTERNET);
                                }

                                Toast.makeText(mContext, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    });

    }

}
