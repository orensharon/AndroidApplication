package com.example.orensharon.finalproject.gui.feed.sections.containers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.sections.Contacts;

/**
 * Created by orensharon on 5/23/15.
 */
public class ContainerContactsFragment extends BaseContainerFragment {


    private boolean mIsViewInited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_section_container, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mIsViewInited) {
            mIsViewInited = true;
            initView();
        }
    }

    private void initView() {

        replaceFragment(new Contacts(), "contacts", false);
    }
}