package com.example.orensharon.finalproject.logic.requests;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orensharon on 1/19/15.
 */


public class MyStringRequest extends StringRequest {

    private JSONObject mBody;

    public MyStringRequest(int method, String url, JSONObject body,
                           Response.Listener<String> listener, Response.ErrorListener errorListener) {


        super(method, url, listener, errorListener);
        mBody = body;
    }


}
