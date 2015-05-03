package com.example.orensharon.finalproject.gui.feed;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.feed.controls.FeedItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedItemAdapter;
import com.example.orensharon.finalproject.sessions.SystemSession;

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

        view = inflater.inflate(R.layout.fragment_feed_section, container, false);


        mFeedItemListView = (ListView) view.findViewById(R.id.feed_items_list_view);

        FeedItem[] feedItems = new FeedItem[10];
        for (int i = 0; i < feedItems.length ; i++) {
            feedItems[i] = new FeedItem(i, "http://www.weebly.com/uploads/1/1/2/9/11298616/8590025_orig.png?0");
        }

        mFeedItemAdapter = new FeedItemAdapter(getActivity(),
                R.layout.control_list_view_row_feed_items,
                feedItems);

        mFeedItemListView.setAdapter(mFeedItemAdapter);

        return view;
    }






}

