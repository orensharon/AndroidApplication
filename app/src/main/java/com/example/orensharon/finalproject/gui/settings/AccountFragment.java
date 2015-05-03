package com.example.orensharon.finalproject.gui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.login.LoginActivity;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 1/31/15.
 */
public class AccountFragment extends Fragment {

    private IFragment mListener;

    public AccountFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mListener = (IFragment)activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_account, container, false);

        Button logoutButton = (Button) view.findViewById(R.id.button_logout);

        View.OnClickListener mLoginButton_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LogOut();
            }
        };

        // Assign click listener to the Login button
        logoutButton.setOnClickListener(mLoginButton_onClick);
        return view;
    }

    private void LogOut() {

        SystemSession systemSession;
        systemSession = new SystemSession(getActivity());
        systemSession.Logout();
        LoadLoginActivity();
    }



    private void LoadLoginActivity() {

        Intent intent;
        intent = new Intent(getActivity(), LoginActivity.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Staring Login Activity
        startActivity(intent);

        // Finish this activity
        getActivity().finish();
    }


}
