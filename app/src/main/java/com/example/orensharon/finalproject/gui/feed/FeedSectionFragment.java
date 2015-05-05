package com.example.orensharon.finalproject.gui.feed;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.feed.controls.FeedItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedItemAdapter;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by orensharon on 1/4/15.
 */
public class FeedSectionFragment extends Fragment {

    private IFragment mListener;
    private FeedItemAdapter mFeedItemAdapter;
    private ListView mFeedItemListView;

    // TODO: change the method - so there will be one HTTP post and load the html into the web view

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (IFragment)activity;
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
        SystemSession systemSession = new SystemSession(getActivity());
        ContentSession contentSession = new ContentSession(getActivity());

        view = inflater.inflate(R.layout.fragment_feed_loading, container, false);

        // TODO: request list of contents from server
        /*
        mFeedItemListView = (ListView) view.findViewById(R.id.feed_items_list_view);

        Map<String,String> listOfItems;

        listOfItems = contentSession.getToBackupList(ApplicationConstants.BACK_UP_LIST_PHOTOS);
        int length = listOfItems.size();

        FeedItem[] feedItems = new FeedItem[length];

        int i = 0;
        for (Map.Entry<String, String> entry : listOfItems.entrySet())
        {
            // TODO: Check if item exist in local storage
            feedItems[i] = new FeedItem(i, "http://www.weebly.com/uploads/1/1/2/9/11298616/8590025_orig.png?0");
            i++;
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

        mFeedItemAdapter = new FeedItemAdapter(getActivity(),
                R.layout.control_list_view_row_feed_items,
                feedItems);

        mFeedItemListView.setAdapter(mFeedItemAdapter);
        */
        //view = inflater.inflate(R.layout.fragment_feed_not_available, container, false);
        return view;
    }






}

