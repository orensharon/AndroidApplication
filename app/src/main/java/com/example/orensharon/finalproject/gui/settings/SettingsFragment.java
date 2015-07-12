package com.example.orensharon.finalproject.gui.settings;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.settings.controls.ContentListAdapter;
import com.example.orensharon.finalproject.gui.settings.controls.Content;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.db.ContentBL;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;

import java.util.ArrayList;

/**
 * Created by orensharon on 1/31/15.
 */
public class SettingsFragment extends Fragment {

    private IFragment mListener;


    private ContentListAdapter mContentListAdapter;
    private ListView mContentsListView;
    private Switch mServiceEnableSwitch, mWifiOnlySwitch;
    private Button mSyncNowButton;
    private RelativeLayout mSyncButtonContainer;


    private ContentBL mContentBL;

    private SettingsSession mSettingsSession;
    private SystemSession mSystemSession;


    public SettingsFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mContentBL = new ContentBL(getActivity());
        mSettingsSession = new SettingsSession(getActivity());
        mSystemSession = new SystemSession(getActivity());
    }



    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mListener = (IFragment)activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_settings, container, false);

        initView(view);

        return view;
    }

    // Init the view components
    private void initView(View view) {
        mContentsListView = (ListView) view.findViewById(R.id.content_list_view);

        mSyncNowButton = (Button)view.findViewById(R.id.sync_now_button);
        mSyncButtonContainer = (RelativeLayout)view.findViewById(R.id.sync_now_button_container);

        initSyncButton();
        initSyncButtonListener();

        mServiceEnableSwitch = (Switch) view.findViewById(R.id.switch_enable_service);
        mWifiOnlySwitch = (Switch) view.findViewById(R.id.switch_wifi_only);


        // Set the switch according what the user saved options
        mServiceEnableSwitch.setChecked(mSettingsSession.getServiceIsEnabledByUser());
        mWifiOnlySwitch.setChecked(mSettingsSession.getWIFIOnly());


        initServiceEnableListener();
        initWIFIOnlyListener();


        // Check the current state before we display the list
        mContentsListView.setEnabled(mServiceEnableSwitch.isChecked());

        mWifiOnlySwitch.setEnabled(mSettingsSession.getServiceIsEnabledByUser());
        LoadContentOptionsIntoListView();
    }

    // Init the status of the sync button
    public void initSyncButton() {

        // Init the sync now button according system state
        int count;

        // With null parameters means check all the lists
        count = mContentBL.getAllUnsyncedContents(null).size();

        // Hiding the button if in the middle of sync or service if off or nothing to sync
        if (!mSystemSession.getInSync(null) &&
                mSettingsSession.getServiceIsEnabledByUser() && count > 0) {

            // Means there is unsynced content
            mSyncNowButton.setEnabled(true);
            mSyncNowButton.setText(getActivity().getString(R.string.sync_now_button_text));
        } else {

            if (count == 0) {
                // All Synced
                mSyncNowButton.setBackgroundColor(getResources().getColor(R.color.online));
                mSyncNowButton.setText(getActivity().getString(R.string.all_synced_button_text));
            }

            // Means service is turned off or nothing to sync
            mSyncNowButton.setEnabled(false);
        }

    }

    // Init the sync button click listener
    private void initSyncButtonListener() {
        View.OnClickListener mSyncNowButton_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Make sure service is running
                if (ObserverService.getServiceStatus() == ObserverService.STATUS_SERVICE_RUNNING) {

                    // Make sure that not in a middle of sync
                    if (!mSystemSession.getInSync(null)) {
                        // Start the sync
                        ((SettingsActivity)mListener).mObserverService.Sync();
                    }

                }
            }
        };
        // Assign click listener to the Login button
        mSyncNowButton.setOnClickListener(mSyncNowButton_onClick);
    }


    // Switch listeners
    private void initServiceEnableListener() {
        // Attach a listener to check for changes in state
        mServiceEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                mSettingsSession.setServiceIsEnabledByUser(isChecked);

                mContentsListView.setEnabled(isChecked);
                mWifiOnlySwitch.setEnabled(isChecked);

                toggleCheckBoxEnable(isChecked);


                if (isChecked == true) {
                    ((SettingsActivity)getActivity()).startObservingService();
                    Log.e("sharonlog", "SHOULD START");
                } else {
                    Log.e("sharonlog", "SHOULD STOP");
                    ((SettingsActivity)getActivity()).stopObservingService();
                }

                initSyncButton();
            }


        });

    }


    private void toggleCheckBoxEnable(boolean isChecked) {
        for(int i=0 ; i< mContentsListView.getCount() ; i++){
            CheckBox cb = (CheckBox)mContentsListView.getChildAt(i).findViewById(R.id.list_view_check_box);
            cb.setEnabled(isChecked);
        }
    }

    private void initWIFIOnlyListener() {

        // Attach a listener to check for changes in state
        mWifiOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                mSettingsSession.setWIFIOnly(isChecked);

            }
        });

    }


    private void LoadContentOptionsIntoListView() {

        // This method will load all the content backup options
        // Into the list view

        Resources res;
        ArrayList<Content> contents;
        String[] options;


        res = getActivity().getResources();

        // Reading items from contents xml resource
        contents = new ArrayList<Content>();
        options = res.getStringArray(R.array.contenttypes);

        // Add each item the list view
        for (String option : options) {

            // Read the saved setting of this content from the system saved data
            contents.add(new Content(option));
        }

        mContentListAdapter = new ContentListAdapter(getActivity(), contents);
        mContentsListView.setAdapter(mContentListAdapter);

    }



}
