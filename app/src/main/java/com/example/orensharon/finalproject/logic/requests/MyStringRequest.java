package com.example.orensharon.finalproject.logic.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by orensharon on 1/19/15.
 */


public class MyStringRequest extends StringRequest {

    public MyStringRequest(int method, String url,
                           Response.Listener<String> listener, Response.ErrorListener errorListener) {


        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return super.getBody();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }
}
