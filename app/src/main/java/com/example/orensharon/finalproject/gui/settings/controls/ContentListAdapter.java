package com.example.orensharon.finalproject.gui.settings.controls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.settings.SettingsActivity;
import com.example.orensharon.finalproject.gui.settings.SettingsFragment;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.sessions.SettingsSession;

import java.util.ArrayList;

/**
 * Created by orensharon on 11/30/14.
 * This class is the adapter for the list view.
 * It will load the items into it from the resource
 */
public class ContentListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Content> mObjects;

    private SettingsSession mSettingsSession;


    public ContentListAdapter(Context context, ArrayList<Content> contents) {

        mContext = context;
        mObjects = contents;
        mSettingsSession = new SettingsSession(context);

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        //if we are at the first position then return 0, last 1, other 2
        if (position == this.getCount() - 1) {
            return 1;
        } else if (position == 0) {
            return 0;
        }
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // For each item in the list - add it

        View view;

        view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.control_list_view_row_settings_contents, parent, false);
        }

        AddContentItem(position, view);

        return view;
    }



    private void AddContentItem(int position, View view) {

        Content c = getContent(position);

        // Fill the content title
        ((TextView) view.findViewById(R.id.list_view_title)).setText(c.getTitle());

        // Setting the details icon
        ImageView detailsIcon;
        detailsIcon = (ImageView)view.findViewById(R.id.list_view_details_icon);

        // Adding details icon
        detailsIcon.setImageResource(R.drawable.arrow);


        // Set the checkbox and its listener
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_view_check_box);

        checkBox.setTag(position);
        checkBox.setEnabled(mSettingsSession.getServiceIsEnabledByUser());
        checkBox.setChecked(mSettingsSession.getUserContentItem(c.getTitle()));
        checkBox.setOnCheckedChangeListener(myCheckChangList);

    }

    private Content getContent(int position) {
        return ((Content) getItem(position));
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            Content content;
            CheckBox myCheckBox = (CheckBox)buttonView;

            content = getContent((Integer) buttonView.getTag());

            if (myCheckBox.isPressed()) {
                mSettingsSession.setUserContentItem(content.getTitle(), isChecked);
                updateObservingService(isChecked, content.getTitle());
            }



        }
    };

    private void updateObservingService(boolean state, String type) {

        SettingsActivity activity;

        activity = (SettingsActivity)mContext;

        if (activity != null) {

            if (activity.mBound) {
                if (state) {
                    activity.mObserverService.StartObserver(type);
                } else {
                    activity.mObserverService.StopObserver(type);
                }
            }

        }



    }



}


