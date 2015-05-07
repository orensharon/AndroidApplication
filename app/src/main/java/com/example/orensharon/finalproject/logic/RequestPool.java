package com.example.orensharon.finalproject.logic;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
* Created by orensharon on 1/27/15.
*/
public class RequestPool {


    private static RequestPool mInstance;

    private RequestQueue mRequestQueue;

    private static Context mContext;

    private RequestPool(Context context) {

        mContext = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized RequestPool getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new RequestPool(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);
    }
}


