package com.example.orensharon.finalproject.gui.feed.sections.containers;

import android.app.Activity;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactAdapter;
import com.example.orensharon.finalproject.gui.feed.controls.FeedContactItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoItem;
import com.example.orensharon.finalproject.gui.feed.controls.FeedPhotoAdapter;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.Helper;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

