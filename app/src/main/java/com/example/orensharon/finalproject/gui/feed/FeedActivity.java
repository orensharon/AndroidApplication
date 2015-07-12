package com.example.orensharon.finalproject.gui.feed;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.BaseContainerFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerContactsFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerPhotosFragment;
import com.example.orensharon.finalproject.gui.settings.SettingsActivity;
import com.example.orensharon.finalproject.logic.CustomImageDownloader;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.HashMap;

/**
 * Created by orensharon on 1/21/15.
 * This is the activity of the feed. It will request the data from the
 * pc server and will show the contents of the user.
 */
public class FeedActivity extends FragmentActivity implements IFragment {

    // Tabs fields
    private FragmentTabHost mTabHost;
    public static final String FEED_PHOTOS = "photos";
    public static final String FEED_CONTACTS = "contacts";

    private final int FADE_TIME_MILLI = 1000;

    public enum Tabs {

        PHOTOS(FEED_PHOTOS, ContainerPhotosFragment.class),
        CONTACTS(FEED_CONTACTS, ContainerContactsFragment.class);

        private String mTabString;
        private Class mClass;

        private Tabs(String str, Class cls) {
            this.mTabString = str;
            this.mClass = cls;
        }

        public String getTabString() { return mTabString; }

        public Class getTabClass() { return mClass; }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        createCustomActionBarTitle();

        initImageLoader();
        initView();


    }

    // Init and configure that loading the images from safe
    private void initImageLoader() {

        SystemSession systemSession = new SystemSession(this);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(ApplicationConstants.HEADER_AUTHORIZATION, systemSession.getToken());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.image_not_found) // resource or drawable
                .showImageOnFail(R.drawable.image_not_found) // resource or drawable
                .imageScaleType(ImageScaleType.EXACTLY)
                .extraForDownloader(headers)
                .displayer(new FadeInBitmapDisplayer(FADE_TIME_MILLI))
                .cacheInMemory(false) // default
                .cacheOnDisk(true) // default
                .build();


        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
            .defaultDisplayImageOptions(options)
            .imageDownloader(new CustomImageDownloader(this))
            .build();


        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ImageLoader.getInstance() != null) {
            ImageLoader.getInstance().destroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            LoadActivity(SettingsActivity.class, true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void LoadFragment(Fragment fragment,String tag, boolean isSupportBack) {

        // The implementation of the IFragment interface

        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        if (isSupportBack) {
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag).addToBackStack(null).commitAllowingStateLoss();
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag).commitAllowingStateLoss();
        }

    }

    @Override
    public void LoadActivity(Class cls, boolean isSupportBack) {

        // Load a given the activity

        Intent intent;
        intent = new Intent(this, cls);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);

        // Check if need to support the back button
        if (!isSupportBack) {
            // Close this activity
            finish();
        }
    }


    private void createCustomActionBarTitle(){

        // Customize the action bar title, fonts and alignments

        int actionBarTitle;
        TextView actionBarTitleView;

        actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");

        actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        actionBarTitleView.setTextColor(Color.WHITE);
        Typeface font = Typeface.createFromAsset(getAssets(),  ApplicationConstants.CUSTOM_FONT_PATH);

        //if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(font);
        //}

    }

    private void initView() {
        AddTabs();
    }

    private void AddTabs() {

        // Adding the tabs
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        // Adding each tab
        for (Tabs tab : Tabs.values()) {

            String tabString;
            tabString = tab.getTabString();

            mTabHost.addTab(
                    mTabHost.newTabSpec(tabString).setIndicator(tabString, null),
                    tab.getTabClass(), null);
        }
    }

    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = mTabHost.getCurrentTabTag();
        if (currentTabTag.equals(FEED_PHOTOS)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(FEED_PHOTOS)).popFragment();
        } else if (currentTabTag.equals(FEED_CONTACTS)) {
            isPopFragment = ((BaseContainerFragment)getSupportFragmentManager().findFragmentByTag(FEED_CONTACTS)).popFragment();
        }

        if (!isPopFragment) {
            finish();
        }
    }


}
