package com.example.orensharon.finalproject.gui.feed.sections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactAdapter;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoAdapter;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoItem;
import com.example.orensharon.finalproject.gui.feed.sections.containers.BaseContainerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 5/23/15.
 */
public class Contacts extends Base {

    private FeedContactAdapter mFeedContactAdapter;
    private ListView mFeedItemListView;


    private ProgressBar mProgressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_feed_tab_section, container, false);

        mFeedItemListView = (ListView) view.findViewById(R.id.feed_items_list_view);
        mFeedItemListView.setClickable(true);
        mFeedItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object o = mFeedItemListView.getItemAtPosition(position);

                ContactDetails fragment = new ContactDetails();
                // if U need to pass some data
                Bundle bundle = new Bundle();

                FeedContactItem feedContactItem = mFeedContactAdapter.getData(position);
                bundle.putString("data", feedContactItem.getData());


                fragment.setArguments(bundle);
                ((BaseContainerFragment)getParentFragment()).replaceFragment(fragment, "contacts", true);
            }
        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.feed_loading_progress);

        mProgressBar.setVisibility(View.VISIBLE);


        Response.Listener response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {

                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);

                    String url = "http://" +
                            mSystemSession.geIPAddressOfSafe() + ApplicationConstants.CONTACT_GET_API_SUFFIX;

                   LoadContacts(response);


                }


            }
        };



        GetListOfContents(
                response,
                ApplicationConstants.CONTACT_GET_API_SUFFIX,
                1);
        return view;
    }


    private void LoadContacts(String data) {

        // Extracting given data string into Json array and iterate over
        // All given photos
        JSONObject json = null;
        JSONArray jsonArray = null;
        try {
            json = new JSONObject(data);
            jsonArray = json.getJSONArray("GetContactsResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonArray != null) {

            int length = jsonArray.length();
            FeedContactItem[] feedItems = new FeedContactItem[length];

            if (length > 0 ) {

                for (int i = 0; i < length; i++) {



                    try {
                        String displayName = jsonArray.getJSONObject(i).get("DisplayName").toString();
                        feedItems[i] = new FeedContactItem(i, displayName, jsonArray.getJSONObject(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        feedItems[i] = new FeedContactItem(i, "","");
                    }



                }

                mFeedContactAdapter = new FeedContactAdapter(getActivity(),
                        R.layout.control_list_view_row_feed_contact_item,
                        feedItems, mSystemSession.getToken());

                mFeedItemListView.setAdapter(mFeedContactAdapter);

            } else {

                // No items to show yet...
                ShowErrorMessage("Nothing to show", R.drawable.icon_info);
            }
        }







    }


}
