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

public class FeedActivity extends FragmentActivity implements IFragment {

    //protected boolean mIsLogged = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        createCutomActionBarTitle();
        LoadFragment(new FeedTabbedFragment());


    }


    private void createCutomActionBarTitle(){

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        actionBarTitleView.setTextColor(Color.WHITE);
        Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/LHANDW.TTF");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(robotoBoldCondensedItalic);
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
            ShowActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void ShowActivity() {


        Intent intent;

        // Sending data to the activity
        intent = new Intent(this, SettingsActivity.class);

        // Stating the activity
        startActivityForResult(intent, 1);
    }


    protected void LoadViewIntoLayout(int resource, int parentLayoutResource, int childViewResource) {

        // Initializing and adding screen parts from the given:
        // 1. resource - the source activity
        // 2. parentLayoutResource - the layout which the child will be added to
        // 3/ childViewResource - the view to add to the layout

        View child;
        LinearLayout parentActivity, parentLayout;
        LayoutInflater inflater;

        inflater = getLayoutInflater();

        // Get the parent layout
        parentActivity = (LinearLayout) inflater.inflate (resource, null);
        parentLayout = (LinearLayout) parentActivity.findViewById(parentLayoutResource);

        // Get the child as view
        child = inflater.inflate(childViewResource, null);

        // Adding the child to the parent layout
        parentLayout.addView(child);

        // Finally set the content view
        setContentView(parentActivity);
    }

    @Override
    public void LoadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
