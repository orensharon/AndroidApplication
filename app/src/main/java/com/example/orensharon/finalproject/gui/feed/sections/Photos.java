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


        Response.Listener response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {

                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);

                    String url = "http://" +
                            mSystemSession.geIPAddressOfSafe() + ApplicationConstants.PHOTO_GET_API_SUFFIX;

                    LoadPhotos(response, url);


                }


            }
        };



        GetListOfContents(
                response,
                ApplicationConstants.PHOTO_GET_LIST_API_SUFFIX,
                1);
        return view;
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
                        String geoLocation = jsonArray.getJSONObject(i).get("GeoLocation").toString();
                        String photoUrl = url + id;
                        feedItems[i] = new FeedPhotoItem(i, photoUrl, dateCreated,geoLocation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        feedItems[i] = new FeedPhotoItem(i, "", "", "");
                    }



                }
                try {
                    if (getActivity() != null) {
                        mFeedPhotoAdapter = new FeedPhotoAdapter(getActivity(),
                                R.layout.control_list_view_row_feed_photo_item,
                                feedItems, mSystemSession.getToken());

                        mFeedItemListView.setAdapter(mFeedPhotoAdapter);
                    }
                } catch (Exception e) {
                    Log.e("","");
                }

            } else {

                // No items to show yet...
                ShowErrorMessage("Nothing to show", R.drawable.icon_info);
               // LoadMessageIntoSection("Nothing to show", R.drawable.icon_info);
            }
        }







    }
}
