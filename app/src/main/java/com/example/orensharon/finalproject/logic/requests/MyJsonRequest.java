package com.example.orensharon.finalproject.logic.requests;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.utils.MD5Checksum;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by orensharon on 4/17/15.
 */
public class MyJsonRequest extends JsonRequest<String> {

    private String mToken, mBody;

    public MyJsonRequest(int method,
                         String url,
                         String requestBody,
                         String token,
                         Response.Listener<String> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);

        mToken = token;
        mBody = requestBody;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        // TODO: prevent from login request, ip get request from hash functioning
        String localHash = MD5Checksum.getMd5Hash(mBody);

        //headers.put("Accept", "application/json");
        headers.put(ApplicationConstants.HEADER_CONTENT_MD5, localHash);

        // If mToken == null it means the request is loginRequest and there
        // isn't token yet
        if (mToken != null) {
            headers.put(ApplicationConstants.HEADER_AUTHORIZATION, mToken);
        }

        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {

        String json;

        try {
            json = new String(networkResponse.data,
                    HttpHeaderParser.parseCharset(networkResponse.headers));

            return Response.success(json,
                    HttpHeaderParser.parseCacheHeaders(networkResponse));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {

        // Parser for the network error

        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {

            // In case of http error
            return volleyError;

        } else {

            // In case of: TimeoutError, NoConnectionError ....
            String errorMessage = new String(volleyError.toString());

            // TODO: Change the error checking method
            if (errorMessage.contains("TimeoutError")) {
                errorMessage = "The server is unreachable.\nPlease try again later";
            } else if (errorMessage.contains("NoConnectionError")) {
                errorMessage = "Please check your internet connectivity and then try again";
            }

            // Other errors
            return new VolleyError(errorMessage);
        }
    }
}
