package com.example.orensharon.finalproject.gui.feed;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.ContentSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 5/4/15.
 */
public class FeedManagerFragment extends Fragment {

    private IFragment mListener;

    public FeedManagerFragment() {

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mListener = (IFragment)activity;

        RequestSafeIP();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;
        SystemSession systemSession = new SystemSession(getActivity());
        ContentSession contentSession = new ContentSession(getActivity());

        view = inflater.inflate(R.layout.fragment_feed_loading, container, false);

        return view;
    }

    // TODO: move method form here and send response, error as params.
    private void RequestSafeIP() {

        // Create a request to server to get the ip address of the safe

        final SystemSession systemSession = new SystemSession(getActivity());

        if (systemSession.geIPAddressOfSafe().equals(ApplicationConstants.NO_IP_VALUE)) {


            RequestFactory requestFactory = new RequestFactory(getActivity());
            JSONObject body = new JSONObject();

            requestFactory.createJsonRequest(
                    Request.Method.POST,
                    ApplicationConstants.IP_GET_API, body.toString(), systemSession.getToken(),
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
                                systemSession.setIPAddressOfSafe(ip);
                                Toast.makeText(getActivity(), systemSession.geIPAddressOfSafe(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                //TODO:// RequestSafeIP(token); with retry policy
                                systemSession.setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);
                                Toast.makeText(getActivity(), "NO-IP",
                                        Toast.LENGTH_LONG).show();
                            }
                            mListener.LoadFragment(new FeedTabbedFragment(), "FEED_TABBED_FRAGMENT", false);
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


                                }

                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();
                            }


                            if (errorMessage != null) {
                                Toast.makeText(getActivity(), errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                            //LoadFragment(new FeedTabbedFragment());
                        }
                    });

        }  else {
            // Reload current fragment
            mListener.LoadFragment(new FeedTabbedFragment(),"FEED_TABBED_FRAGMENT", false);
        }
    }

}
