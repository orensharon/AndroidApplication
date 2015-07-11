package com.example.orensharon.finalproject.gui.feed.sections.containers;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;

/**
 * Created by orensharon on 1/4/15.
 */
public class BaseContainerFragment extends Fragment {

    private IFragment mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (IFragment)activity;


    }


    public void replaceFragment(Fragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.container_framelayout, fragment, tag);
        transaction.commitAllowingStateLoss();
        getChildFragmentManager().executePendingTransactions();
    }

    public boolean popFragment() {

        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }
}

