package com.example.orensharon.finalproject.gui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.example.orensharon.finalproject.gui.IFragment;

import com.example.orensharon.finalproject.R;

public class SettingsActivity extends FragmentActivity implements IFragment {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            LoadFragment(new SettingsFragment(), false);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:

                LoadFragment(new AccountFragment(), true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void LoadFragment(Fragment fragment, boolean isSupportBack) {

        // The implementation of the IFragment interface

        FragmentTransaction fragmentTransaction;
        FragmentManager fragmentManager;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        if (isSupportBack) {
            fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
        }

    }

    @Override
    public void LoadActivity(Class cls, boolean isSupportBack) {

        // Load a given the activity

        Intent intent;
        intent = new Intent(this, cls);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);

        // Check if need to support the back button
        if (!isSupportBack) {
            // Close this activity
            finish();
        }
    }


}
