package com.example.orensharon.finalproject.gui.settings;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.settings.controls.ContentListAdapter;
import com.example.orensharon.finalproject.gui.settings.controls.Content;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.service.db.ContentBL;
import com.example.orensharon.finalproject.sessions.SettingsSession;

import java.util.ArrayList;

/**
 * Created by orensharon on 1/31/15.
 */
public class SettingsFragment extends Fragment {

    private IFragment mListener;
    private ContentListAdapter mContentListAdapter;
    private ListView mContentsListView;
    private TextView mListDescriptionTextView;
    private Switch mServiceEnableSwitch, mWifiOnlySwitch;
    private Button mSyncNowButton;

    private ContentBL mContentBL;

    private SettingsSession mSettingsSession;

    public SettingsFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mContentBL = new ContentBL(getActivity());
    }



    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mListener = (IFragment)activity;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_settings, container, false);


        mSettingsSession = new SettingsSession(getActivity());

        mContentsListView = (ListView) view.findViewById(R.id.content_list_view);
        mListDescriptionTextView = (TextView) view.findViewById(R.id.text_view_content_list_description);

        mSyncNowButton = (Button)view.findViewById(R.id.sync_now_button);

        toggleEnableSyncNowButton();
        initSyncButtonListener();

        mServiceEnableSwitch = (Switch) view.findViewById(R.id.switch_enable_service);
        mWifiOnlySwitch = (Switch) view.findViewById(R.id.switch_wifi_only);


        // Set the switch according what the user saved options
        mServiceEnableSwitch.setChecked(mSettingsSession.getServiceIsEnabledByUser());
        mWifiOnlySwitch.setChecked(mSettingsSession.getWIFIOnly());




        initServiceEnableListener();
        initWIFIOnlyListener();




        // Check the current state before we display the list
        mListDescriptionTextView.setVisibility(((mServiceEnableSwitch.isChecked() == true) ? View.VISIBLE : View.INVISIBLE));
        mContentsListView.setVisibility(((mServiceEnableSwitch.isChecked() == true) ? View.VISIBLE : View.INVISIBLE));

        mWifiOnlySwitch.setEnabled(mSettingsSession.getServiceIsEnabledByUser());
        LoadContentOptionsIntoListView();

        return view;
    }


    private void toggleEnableSyncNowButton() {

        // create click listener



        if (mContentBL.getAllUnsyncedContents(null).size() > 0) {
            // Means there is unsynced content
            mSyncNowButton.setEnabled(true);
        } else {
            mSyncNowButton.setText("All synced :)");
            mSyncNowButton.setEnabled(false);
        }

        mSyncNowButton.setEnabled(mSettingsSession.getServiceIsEnabledByUser());
        mSyncNowButton.setEnabled(false);

    }

    private void initSyncButtonListener() {
        View.OnClickListener mSyncNowButton_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Make sure service is running

                if (ObserverService.getServiceStatus() == ObserverService.STATUS_SERVICE_RUNNING) {

                   // TODO: send message to service
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

                mListDescriptionTextView.setVisibility(((isChecked == true) ? View.VISIBLE : View.INVISIBLE));
                mContentsListView.setVisibility(((isChecked == true) ? View.VISIBLE : View.INVISIBLE));
                mWifiOnlySwitch.setEnabled(isChecked);

                mSettingsSession.setServiceIsEnabledByUser(isChecked);

                if (isChecked == true) {
                    startObservingService();

                } else {
                    stopObservingService();
                }

                toggleEnableSyncNowButton();
            }
        });

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



    private void startObservingService() {

        // Creating an intent with the selected values of the user
        Intent ServiceIntent;
        ServiceIntent = new Intent(getActivity(), ObserverService.class);


        // Service will start once, any call after that will only send
        // the intent to communicate with the service this way
        getActivity().startService(ServiceIntent);


    }
    private void stopObservingService() {
        // Stop the service
        Intent mServiceIntent;
        mServiceIntent = new Intent(getActivity(), ObserverService.class);
        getActivity().stopService(mServiceIntent);
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
