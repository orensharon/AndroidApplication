package com.example.orensharon.finalproject.gui.feed;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.settings.SettingsActivity;
/**
 * Created by orensharon on 1/21/15.
 * This is the activity of the feed. It will request the data from the
 * pc server and will show the contents of the user.
 */
public class FeedActivity extends FragmentActivity implements IFragment {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        createCustomActionBarTitle();
        LoadFragment(new FeedTabbedFragment());

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
            ShowSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void LoadFragment(Fragment fragment) {

        // The implementation of the IFragment interface

        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    private void ShowSettingsActivity() {


        Intent intent;

        // Sending data to the activity
        intent = new Intent(this, SettingsActivity.class);

        // Stating the activity
        startActivityForResult(intent, 1);
    }
    private void createCustomActionBarTitle(){

        // Customize the action bar title, fonts and alignments

        int actionBarTitle;
        TextView actionBarTitleView;

        actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");

        actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        actionBarTitleView.setTextColor(Color.WHITE);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/LHANDW.TTF");

        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(font);
        }

    }

}
