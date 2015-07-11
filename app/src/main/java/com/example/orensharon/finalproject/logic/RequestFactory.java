package com.example.orensharon.finalproject.logic;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.logic.requests.MultipartRequest;
import com.example.orensharon.finalproject.logic.requests.MyJsonRequest;
import com.example.orensharon.finalproject.logic.requests.MyStringRequest;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by orensharon on 1/27/15.
 * Request Factory
 */
public class RequestFactory {

    private Context mContext;

    public RequestFactory(Context context) {
        mContext = context;
    }


    public void createJsonRequest(int method, String url, String typeOfContent, String body, String token,
                                  Response.Listener<String> listener, Response.ErrorListener errorListener) {

        // Creating JSON request and add it to pool

        MyJsonRequest request = new MyJsonRequest(
            method,
            url,
            body,
            token,
            listener,
            errorListener
        );

        request.setTag(typeOfContent);

        //request.setRetryPolicy(new DefaultRetryPolicy(
        //        (int) TimeUnit.SECONDS.toMillis(20),
        //        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        //        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestPool.getInstance(mContext).addToRequestQueue(request);

    }

    public void createStringRequest(int method, String url, JSONObject body,
                                    Response.Listener<String> listener, Response.ErrorListener errorListener) {

        // Creating String request and add it to pool

        MyStringRequest request = new MyStringRequest(
                method,
                url,
                body,
                listener,
                errorListener);




        // Adding request to queue
        RequestPool.getInstance(mContext).addToRequestQueue(request);


    }

    public void createMultipartRequest(String url, String typeOfContent, File file, JSONObject body,
                                       String token, Response.Listener listener, Response.ErrorListener errorListener) {

        // Creating Multipart request and add it to pool
        if (listener != null && errorListener != null) {
            MultipartRequest request = new MultipartRequest(
                    url,
                    file,
                    body,
                    token,
                    errorListener,
                    listener
            );

            request.setTag(typeOfContent);
            //request.setRetryPolicy(new DefaultRetryPolicy(20000,
            //                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Adding request to queue
            RequestPool.getInstance(mContext).addToRequestQueue(request);
        }
    }

    public void CancelByTag(String tag) {

        // Cancel all queued requests by given tag
        Log.i("sharonlog", "canceling " + tag +  " request...");
        RequestPool.getInstance(mContext).getRequestQueue().cancelAll(tag);
    }

    public void CancelAll() {

        // Cancel all queued requests
        Log.i("sharonlog", "canceling all request...");
        RequestPool.getInstance(mContext).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }



}


