package com.example.orensharon.finalproject.gui.feed.sections;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.sections.containers.BaseContainerFragment;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

public class Base extends Fragment {

    protected SystemSession mSystemSession;


    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mSystemSession = new SystemSession(this.getActivity());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    protected void GetListOfContents(final Response.Listener reqResponse, final String apiSuffix,
            final int ipRequestRetriesCount) {

        // Create a request to safe to get list of contents

        if (mSystemSession.geIPAddressOfSafe().equals(ApplicationConstants.NO_IP_VALUE)) {

            // Don't have saved IP address - get it first
            RequestSafeIP(reqResponse, apiSuffix);
        } else {

            // Got saved IP address
            final String url = "http://" + mSystemSession.geIPAddressOfSafe() + apiSuffix;

            RequestFactory requestFactory = new RequestFactory(getActivity());
            JSONObject body = new JSONObject();


            requestFactory.createJsonRequest(
                    Request.Method.POST,
                    url,
                    "list_request",
                    body.toString(),
                    mSystemSession.getToken(),
                    reqResponse,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = null;

                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                switch (response.statusCode) {

                                    // 400
                                    case ApplicationConstants.HTTP_BAD_REQUEST:
                                        errorMessage = "Bad request";
                                        break;

                                    // 403
                                    case ApplicationConstants.HTTP_FORBIDDEN:
                                        errorMessage = "You don't have the permission";
                                        break;

                                    // 409
                                    case ApplicationConstants.HTTP_CONFLICT:
                                        errorMessage = "";
                                        break;

                                    // 500
                                    case ApplicationConstants.HTTP_INTERNAL_SERVER_ERROR:
                                        errorMessage = "Internal server error";
                                        break;

                                }

                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();
                            }


                            if (errorMessage != null) {

                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), errorMessage,
                                            Toast.LENGTH_LONG).show();
                                }

                                if (errorMessage.contains("unreachable")) {
                                    // Request safe IP
                                    if (ipRequestRetriesCount > 0) {

                                        RequestSafeIP(reqResponse, apiSuffix);
                                    }
                                } else if (errorMessage.contains("connectivity")) {


                                        ShowErrorMessage("Check connection", R.drawable.icon_alert);

                                }

                            }


                            if (ipRequestRetriesCount == 0) {

                                // Loads error message into the current tab
                                ShowErrorMessage("Not Available", R.drawable.icon_alert);
                            }
                        }
                    });
        }
    }

    protected void ShowErrorMessage(String message, int icon) {
        Message fragment = new Message();
        Bundle bundle = new Bundle();

        if (getParentFragment().getTag() != null) {
            bundle.putString("section", getParentFragment().getTag().toString());
            bundle.putString("message", message);
            bundle.putInt("icon", icon);

            fragment.setArguments(bundle);


            ((BaseContainerFragment) getParentFragment()).replaceFragment(fragment, "message", false);
        }
    }

    protected void RequestSafeIP(final Response.Listener reqResponse, final String api) {

        // Create a request to server to get the ip address of the safe


        RequestFactory requestFactory = new RequestFactory(getActivity());
        JSONObject body = new JSONObject();

        requestFactory.createJsonRequest(
                Request.Method.POST,
                ApplicationConstants.IP_GET_API,
                "ip_request",
                body.toString(),
                mSystemSession.getToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String ip = null;

                        // Extract the safe IP from the response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ip = jsonObject.getString(ApplicationConstants.IP_GETTER_IP_ADDRESS_KEY);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // null for error reading from json
                        // Check if this is a valid ip address
                        IPAddressValidator ipAddressValidator;
                        ipAddressValidator = new IPAddressValidator();

                        if (ip != null && ipAddressValidator.validate(ip)) {
                            mSystemSession.setIPAddressOfSafe(ip);

                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), mSystemSession.geIPAddressOfSafe(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {

                            mSystemSession.setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);

                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "NO-IP",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        GetListOfContents(reqResponse, api, 0);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = null;

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            switch (response.statusCode) {

                                // 400
                                case ApplicationConstants.HTTP_BAD_REQUEST:
                                    errorMessage = "Bad request";
                                    break;

                                // 403
                                case ApplicationConstants.HTTP_FORBIDDEN:
                                    errorMessage = "You don't have the permission";
                                    break;

                                // 409
                                case ApplicationConstants.HTTP_CONFLICT:
                                    errorMessage = "";
                                    break;

                                // 500
                                case ApplicationConstants.HTTP_INTERNAL_SERVER_ERROR:
                                    errorMessage = "Internal server error";
                                    break;


                            }

                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }


                        if (errorMessage != null) {
                            if (errorMessage.contains("unreachable")) {


                            } else if (errorMessage.contains("connectivity")) {


                            }
                        }

                        ShowErrorMessage("Not available", R.drawable.icon_alert);
                        //LoadFragment(new FeedTabbedFragment());
                    }
                });


    }


}
