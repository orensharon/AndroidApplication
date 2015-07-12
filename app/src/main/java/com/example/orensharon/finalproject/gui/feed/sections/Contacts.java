package com.example.orensharon.finalproject.gui.feed.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactAdapter;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactItem;
import com.example.orensharon.finalproject.gui.feed.sections.containers.BaseContainerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 5/23/15.
 * Contacts Screen bl
 */
public class Contacts extends Base {

    private FeedContactAdapter mFeedContactAdapter;
    private ListView mFeedItemListView;


    private ProgressBar mProgressBar;

    public final static String OBJECT_DATA = "data";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_feed_tab_section, container, false);

        initView(view);
        return view;
    }

    // Init contact screen components
    private void initView(View view) {

        mFeedItemListView = (ListView) view.findViewById(R.id.feed_items_list_view);
        mFeedItemListView.setClickable(true);
        mFeedItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {


                ContactDetails fragment = new ContactDetails();

                Bundle bundle = new Bundle();

                FeedContactItem feedContactItem = mFeedContactAdapter.getData(position);
                bundle.putString(OBJECT_DATA, feedContactItem.getData());


                fragment.setArguments(bundle);
                ((BaseContainerFragment)getParentFragment()).replaceFragment(fragment, FeedActivity.FEED_CONTACTS, true);
            }
        });

        // Show the progress bar
        mProgressBar = (ProgressBar) view.findViewById(R.id.feed_loading_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        // Create response for the list getter
        Response.Listener response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {

                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);

                    LoadData(response);
                }
            }
        };

        // Get all data
        GetListOfContents(
                response,
                ApplicationConstants.CONTACT_GET_API_SUFFIX,
                1);
    }


    // Extracting given data string into Json array and iterate over
    public void LoadData(String data) {

        JSONObject json;
        JSONArray jsonArray = null;
        try {
            json = new JSONObject(data);
            jsonArray = json.getJSONArray(ApplicationConstants.SAFE_RESPONSE_LIST_OF_CONTACTS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray != null) {

            int length = jsonArray.length();
            FeedContactItem[] feedItems = new FeedContactItem[length];

            if (length > 0 ) {

                for (int i = 0; i < length; i++) {

                    try {
                        String displayName =
                                jsonArray.getJSONObject(i).get(ApplicationConstants.CONTACT_DISPLAY_NAME_KEY).toString();
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
                ShowErrorMessage(getActivity().getString(R.string.message_nothing_to_show), R.drawable.icon_info);
            }
        }







    }


}
