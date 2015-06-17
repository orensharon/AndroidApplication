package com.example.orensharon.finalproject.gui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.IFragment;
import com.example.orensharon.finalproject.gui.login.LoginActivity;
import com.example.orensharon.finalproject.service.ObserverService;
import com.example.orensharon.finalproject.sessions.SystemSession;

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
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account, menu);
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

        view = inflater.inflate(R.layout.fragment_settings_account, container, false);

        Button logoutButton = (Button) view.findViewById(R.id.button_logout);
        TextView usernameTextView = (TextView) view.findViewById(R.id.username_text_view);
        TextView safeIPTextView = (TextView) view.findViewById(R.id.safe_ip_text_view);

        SystemSession systemSession = new SystemSession(getActivity());

        usernameTextView.setText(systemSession.getUsername());
        safeIPTextView.setText(systemSession.geIPAddressOfSafe());

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
        ((SettingsActivity)mListener).stopObservingService();

        mListener.LoadActivity(LoginActivity.class, false);
    }






}
