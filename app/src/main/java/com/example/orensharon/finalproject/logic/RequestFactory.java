package com.example.orensharon.finalproject.logic;

import android.content.Context;

import com.android.volley.Response;
import com.example.orensharon.finalproject.logic.requests.MyStringRequest;

import org.json.JSONObject;

/**
 * Created by orensharon on 1/27/15.
 */
public class RequestFactory {

    private Context mContext;

    public RequestFactory(Context context) {
        mContext = context;
    }

    public void createRequest(int method, String url, JSONObject body,
                              Response.Listener<String> listener, Response.ErrorListener errorListener) {



        MyStringRequest request = new MyStringRequest(
                method,
                url,
                listener,
                errorListener);


        // Adding request to queue
        RequestPool.getInstance(mContext).addToRequestQueue(request);


    }


}


