package com.example.orensharon.finalproject.gui.feed;

import android.app.Activity;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactAdapter;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoAdapter;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.Helper;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by orensharon on 1/4/15.
 */
public class FeedSectionFragment extends Fragment {

    private IFragment mListener;
    private FeedPhotoAdapter mFeedPhotoAdapter;
    private FeedContactAdapter mFeedContactAdapter;
    private ListView mFeedItemListView;
    private SystemSession mSystemSession;

    private String mTabTag;

    private ProgressBar mProgressBar;
    private RelativeLayout mMessageContainer, mFeedItemsContainer;
    private TextView mMessageTextView, mRetryTextView;
    private ImageView mMessageImageView;

    // TODO: change the method - so there will be one HTTP post and load the html into the web view

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (IFragment)activity;
        mSystemSession = new SystemSession(getActivity());

        RequestSafeIP();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_feed_tab_section, container, false);

        TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabhost);

        mTabTag = host.getCurrentTabTag();

        mFeedItemListView = (ListView) view.findViewById(R.id.feed_items_list_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.feed_loading_progress);

        mMessageContainer = (RelativeLayout) view.findViewById(R.id.fragment_feed_message_container);
        mMessageTextView = (TextView) view.findViewById(R.id.feed_section_message_text_view);
        mRetryTextView = (TextView) view.findViewById(R.id.retry_load_content_text_view);
        mMessageImageView = (ImageView) view.findViewById(R.id.feed_section_message_image_view);

        initRetryOnClick();

        BuildSection();



        // TODO: Request list of contents from server

        //view = inflater.inflate(R.layout.fragment_feed_not_available, container, false);
        return view;
    }

    private void BuildSection() {
        ApplicationConstants.FeedContentKeys keys = null;
        Response.Listener response = null;

        if (mTabTag.toLowerCase().equals("photos")) {

            keys = ApplicationConstants.FeedContentKeys.PHOTOS;
            final String api = keys.getContentAPI();
            response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {

                        // Hide progress bar
                        mProgressBar.setVisibility( View.GONE );

                        String url = "http://" + mSystemSession.geIPAddressOfSafe() + api;
                        LoadPhotos(response, url);


                    }


                }
            };

        } else if (mTabTag.toLowerCase().equals("contacts")) {

            keys = ApplicationConstants.FeedContentKeys.CONTACTS;
            response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response != null) {


                        // Hide the progress bar
                        mProgressBar.setVisibility( View.GONE );

                        JSONArray contacts = null;

                        String fixed_result = "\"Contacts\":" + response;
                        try {
                            contacts = new JSONArray(fixed_result);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            contacts = null;
                        }

                        if (contacts != null) {
                            int length = contacts.length();
                            FeedContactItem[] feedItems = new FeedContactItem[length];


                            for (int i = 0; i <= contacts.length() ; i ++) {

                                try {
                                    feedItems[i] = new FeedContactItem(i, contacts.get(i).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            mFeedContactAdapter = new FeedContactAdapter(getActivity(),
                                    R.layout.control_list_view_row_feed_photo_item,
                                    feedItems, mSystemSession.getToken());

                            mFeedItemListView.setAdapter(mFeedPhotoAdapter);
                        }
                    }


                }
            };

        }

        if (keys != null) {
            GetListOfContents(response, keys);
        }
    }


    private void GetListOfContents(Response.Listener response, final ApplicationConstants.FeedContentKeys keys) {

        // Create a request to safe to get list of contents


        RequestFactory requestFactory = new RequestFactory(getActivity());
        JSONObject body = new JSONObject();

        String ip = mSystemSession.geIPAddressOfSafe();
        String url = "http://" + ip + keys.getContentListAPI();

        requestFactory.createJsonRequest(
                Request.Method.POST,
                url,
                "list_request",
                body.toString(),
                mSystemSession.getToken(),
                response
                ,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = null;

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            switch (response.statusCode) {

                                // 400
                                case ApplicationConstants.HTTP_BAD_REQUEST:
                                    errorMessage = "Bad request";
                                    break;

                                // 403
                                case ApplicationConstants.HTTP_FORBIDDEN:
                                    errorMessage = "You don't have the permission";
                                    break;

                                // 409
                                case ApplicationConstants.HTTP_CONFLICT:
                                    errorMessage = "MD5 not equal";
                                    break;


                            }

                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }


                        if (errorMessage != null && getActivity() != null) {
                            Toast.makeText(getActivity(), errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                        if (getActivity() !=null) {

                            // Loads error message into the current tab
                            LoadMessageIntoSection("Not Available", R.drawable.icon_alert);
                        }
                    }
                });
    }

    private void LoadPhotos(String data, String url) {

        // Extracting given data string into Json array and iterate over
        // All given photos
        JSONObject json = null;
        JSONArray jsonArray = null;
        try {
            json = new JSONObject(data);
            jsonArray = json.getJSONArray("GetListOfPhotosResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonArray != null) {

            int length = jsonArray.length();
            FeedPhotoItem[] feedItems = new FeedPhotoItem[length];

            if (length > 0 ) {

                for (int i = 0; i < length; i++) {



                    try {
                        String id = jsonArray.getJSONObject(i).get("Id").toString();
                        String dateCreated = jsonArray.getJSONObject(i).get("DateCreated").toString();
                        String photoUrl = url + id;
                        feedItems[i] = new FeedPhotoItem(i, photoUrl, dateCreated);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        feedItems[i] = new FeedPhotoItem(i, "", "");
                    }



                }

                mFeedPhotoAdapter = new FeedPhotoAdapter(getActivity(),
                        R.layout.control_list_view_row_feed_photo_item,
                        feedItems, mSystemSession.getToken());

                mFeedItemListView.setAdapter(mFeedPhotoAdapter);

            } else {

                // No items to show yet...
                LoadMessageIntoSection("Nothing to show", R.drawable.icon_info);
            }
        }







    }

    // TODO: move method form here and send response, error as params.
    private void RequestSafeIP() {

        // Create a request to server to get the ip address of the safe

        final SystemSession systemSession = new SystemSession(getActivity());

        if (systemSession.geIPAddressOfSafe().equals(ApplicationConstants.NO_IP_VALUE)) {


            RequestFactory requestFactory = new RequestFactory(getActivity());
            JSONObject body = new JSONObject();

            requestFactory.createJsonRequest(
                    Request.Method.POST,
                    ApplicationConstants.IP_GET_API,
                    "ip_requst",
                    body.toString(),
                    systemSession.getToken(),
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
                                systemSession.setIPAddressOfSafe(ip);
                                Toast.makeText(getActivity(), systemSession.geIPAddressOfSafe(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                //TODO:// RequestSafeIP(token); with retry policy
                                systemSession.setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);
                                Toast.makeText(getActivity(), "NO-IP",
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
                                        errorMessage = "Bad request";
                                        break;

                                    // 403
                                    case ApplicationConstants.HTTP_FORBIDDEN:
                                        errorMessage = "You don't have the permission";
                                        break;


                                }

                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();
                            }


                            if (errorMessage != null) {
                                Toast.makeText(getActivity(), errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                            //LoadFragment(new FeedTabbedFragment());
                        }
                    });

        } else {
            // Reload current fragment

        }
    }
    private void LoadMessageIntoSection(String message, int res) {
        mMessageTextView.setText(message);
        mMessageImageView.setImageResource(res);

        mMessageContainer.setVisibility( View.VISIBLE );
    }

    private void initRetryOnClick()
    {

        // create click listener

        View.OnClickListener mRetryTextView_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mMessageContainer.setVisibility( View.GONE );
                BuildSection();

            }
        };

        mRetryTextView.setOnClickListener(mRetryTextView_onClick);

    }

}

