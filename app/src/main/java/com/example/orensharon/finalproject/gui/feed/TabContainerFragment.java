package com.example.orensharon.finalproject.gui.feed;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerContactsFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerPhotosFragment;


public class TabContainerFragment extends Fragment {

    private IFragment mListener;

    // Tabs fields
    private FragmentTabHost mTabHost;
    private static final String FEED_PHOTOS = "photos";
    private static final String FEED_CONTACTS = "contacts";
    private static final String FEED_ALL = "all";

    public enum Tabs {

        PHOTOS(FEED_PHOTOS, ContainerPhotosFragment.class),
        CONTACTS(FEED_CONTACTS, ContainerContactsFragment.class),
        ALL(FEED_ALL, null);

        private String mTabString;
        private Class mClass;

        private Tabs(String str, Class cls) {
            this.mTabString = str;
            this.mClass = cls;
        }

        public String getTabString() { return mTabString; }

        public Class getTabClass() { return mClass; }
    }


    public TabContainerFragment() {
        // Empty constructor
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

        // Adding each tab
        for (Tabs tab : Tabs.values()) {

            String tabString;
            tabString = tab.getTabString();

            mTabHost.addTab(
                    mTabHost.newTabSpec(tabString).setIndicator(tabString, null),
                    tab.getTabClass(), null);
        }
    }

}
