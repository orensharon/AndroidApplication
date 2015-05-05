package com.example.orensharon.finalproject.gui.feed;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;


public class FeedTabbedFragment extends Fragment {

    private IFragment mListener;

    // Tabs fields
    private FragmentTabHost mTabHost;
    private static final String FEED_RECENT = "recent";
    private static final String FEED_POPULAR = "popular";
    private static final String FEED_ALL = "all";

    public enum Tabs {

        RECENT(FEED_RECENT),
        POPULAR(FEED_POPULAR),
        ALL(FEED_ALL);

        private String mTabString;

        private Tabs(String str) {
            this.mTabString = str;
        }

        public String getTabString() { return mTabString; }
    }


    public FeedTabbedFragment() {
        // Empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mListener = (IFragment)activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_feed_tabs, container, false);
        AddTabs(view);

        return view;
    }

    private void AddTabs(View view) {

        // Adding the tabs
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        for (Tabs tab : Tabs.values()) {

            String tabString;
            tabString = tab.getTabString();

            mTabHost.addTab(
                    mTabHost.newTabSpec(tabString).setIndicator(tabString, null),
                    FeedSectionFragment.class, null);
        }
    }



}
