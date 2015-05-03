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

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        return super.getHeaders();
    }



    @Override
    protected Map<String, String> getParams() throws AuthFailureError {

        /*Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("username","sharon");
        params.put("password","12345");*/

        //return params;
        return super.getParams();
    }
}
