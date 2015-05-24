package com.example.orensharon.finalproject.gui.feed.sections.containers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.sections.Photos;

/**
 * Created by orensharon on 5/23/15.
 */
public class ContainerPhotosFragment extends BaseContainerFragment {


    private boolean mIsViewInited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_section_container, null);


        return view;
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

        replaceFragment(new Photos(), "photos", false);
    }
}