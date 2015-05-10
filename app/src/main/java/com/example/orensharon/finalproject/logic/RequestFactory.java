package com.example.orensharon.finalproject.logic;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.example.orensharon.finalproject.logic.requests.MultipartRequest;
import com.example.orensharon.finalproject.logic.requests.MyJsonRequest;
import com.example.orensharon.finalproject.logic.requests.MyStringRequest;
import com.example.orensharon.finalproject.sessions.SystemSession;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by orensharon on 1/27/15.
 */
public class RequestFactory {

    private Context mContext;

    public RequestFactory(Context context) {
        mContext = context;
    }


    public void createJsonRequest(int method, String url, String typeOfContent, String body, String token,
                                  Response.Listener<String> listener, Response.ErrorListener errorListener) {

        MyJsonRequest request = new MyJsonRequest(
            method,
            url,
            body,
            token,
            listener,
            errorListener
        );

        request.setTag(typeOfContent);
        RequestPool.getInstance(mContext).addToRequestQueue(request);

    }

    public void createStringRequest(int method, String url, JSONObject body,
                                    Response.Listener<String> listener, Response.ErrorListener errorListener) {



        MyStringRequest request = new MyStringRequest(
                method,
                url,
                body,
                listener,
                errorListener);



        // Adding request to queue
        RequestPool.getInstance(mContext).addToRequestQueue(request);


    }

    public void createMultipartRequest(String url, String typeOfContent, File file, String token, int id,
                                       Response.Listener listener, Response.ErrorListener errorListener) {



        MultipartRequest request = new MultipartRequest(
                url,
                errorListener,
                listener,
                file,
                token,
                id,
                typeOfContent
        );

        request.setTag(typeOfContent);

        // Adding request to queue
        RequestPool.getInstance(mContext).addToRequestQueue(request);
    }

    public void CancelByTag(String tag) {

        Log.i("sharonlog", "canceling " + tag +  " request...");
        RequestPool.getInstance(mContext).getRequestQueue().cancelAll(tag);
    }

    public void CancelAll() {
        Log.i("sharonlog", "canceling all request...");
        RequestPool.getInstance(mContext).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

}


