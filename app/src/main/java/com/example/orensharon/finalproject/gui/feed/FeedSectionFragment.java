package com.example.orensharon.finalproject.gui.feed;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;

/**
 * Created by orensharon on 1/4/15.
 */
public class FeedSectionFragment extends Fragment {

    private IFragment mListener;

    // TODO: change the method - so there will be one HTTP post and load the html into the web view
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (IFragment)activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

//        mWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

//        mWebView.restoreState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.control_web_view, container, false);
        mWebView = (WebView) view.findViewById(R.id.web_view_feed);

        if (savedInstanceState == null) {
            mWebView.loadUrl("http://sharon-se-server.dynu.com/demo.html");
        }

        ReloadWebView(view);


        return view;
    }

    private void ReloadWebView(View view) {

        // Reloads the web view on swiping

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        //final TextView rndNum = (TextView) findViewById(R.id.rndNum);
        swipeView.setColorScheme(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_light);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }
}
