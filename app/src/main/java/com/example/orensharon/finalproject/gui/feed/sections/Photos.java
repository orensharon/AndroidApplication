package com.example.orensharon.finalproject.gui.feed.sections;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoAdapter;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 5/23/15.
 */
public class Photos extends Base {

    private static final int RETRIES = 1;
    private FeedPhotoAdapter mFeedPhotoAdapter;
    private ListView mFeedItemListView;


    private ProgressBar mProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_feed_tab_section, container, false);

        mFeedItemListView = (ListView) view.findViewById(R.id.feed_items_list_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.feed_loading_progress);

        mProgressBar.setVisibility(View.VISIBLE);

        requestDataFromSafe();

        return view;
    }

    // Creating request to get data from safe
    private void requestDataFromSafe() {
        Response.Listener response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {

                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);

                    String url = "http://" +
                            mSystemSession.geIPAddressOfSafe() + ApplicationConstants.PHOTO_GET_API_SUFFIX;

                    LoadData(response, url);
                }
            }
        };

        // Get list with meta data
        GetListOfContents(
                response,
                ApplicationConstants.PHOTO_GET_LIST_API_SUFFIX,
                RETRIES);
    }


    // Extracting given data string into Json array and iterate over all photos
    public void LoadData(String data, String url) {


        JSONObject json;
        JSONArray jsonArray = null;
        try {
            json = new JSONObject(data);
            jsonArray = json.getJSONArray(ApplicationConstants.SAFE_RESPONSE_LIST_OF_PHOTOS);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Got list
        if (jsonArray != null) {

            int length = jsonArray.length();
            FeedPhotoItem[] feedItems = new FeedPhotoItem[length];

            if (length > 0 ) {

                // Create array from the list
                for (int i = 0; i < length; i++) {

                    try {
                        // Read data from list

                        String id = jsonArray.getJSONObject(i).get(ApplicationConstants.CONTENT_ID_KEY).toString();
                        int realId = jsonArray.getJSONObject(i).getInt(ApplicationConstants.PHOTO_READ_ID_KEY);

                        // Read dates
                        String dateCreated =
                                jsonArray.getJSONObject(i).get(ApplicationConstants.PHOTO_DATE_CREATED_KEY).toString();

                        // Read location
                        String geoLocation =
                                jsonArray.getJSONObject(i).get(ApplicationConstants.PHOTO_GEO_LOCATION_KEY).toString();

                        // Read photo url
                        String photoUrl = url + id;
                        feedItems[i] = new FeedPhotoItem(i, realId,photoUrl, dateCreated,geoLocation);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        feedItems[i] = new FeedPhotoItem(i, -1 , "", "", "");
                    }



                }
                try {
                    if (getActivity() != null) {
                        mFeedPhotoAdapter = new FeedPhotoAdapter(getActivity(),
                                R.layout.control_list_view_row_feed_photo_item,
                                feedItems);

                        mFeedItemListView.setAdapter(mFeedPhotoAdapter);
                    }
                } catch (Exception e) {
                    Log.e("","");
                }

            } else {

                // No items to show yet...
                ShowErrorMessage(getActivity().getString(R.string.feed_nothing_to_show_message), R.drawable.icon_info);
            }
        }







    }
}
