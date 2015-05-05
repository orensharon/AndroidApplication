package com.example.orensharon.finalproject.logic;

import android.content.Context;

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


    public void createJsonRequest(int method, String url, String body, String token,
                                  Response.Listener<String> listener, Response.ErrorListener errorListener) {

        MyJsonRequest request = new MyJsonRequest(
            method,
            url,
            body,
            token,
            listener,
            errorListener
        );

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

    public void createMultipartRequest(String url, String typeOfContent, File file, String id,
                                       Response.Listener listener, Response.ErrorListener errorListener) {


        SystemSession systemSession = new SystemSession(mContext);

        MultipartRequest request = new MultipartRequest(
                url,
                errorListener,
                listener,
                file,
                systemSession.getToken(),
                id,
                typeOfContent
        );


        // Adding request to queue
        RequestPool.getInstance(mContext).addToRequestQueue(request);

    }

}


