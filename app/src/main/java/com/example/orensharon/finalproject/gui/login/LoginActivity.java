package com.example.orensharon.finalproject.gui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.sessions.SystemSession;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private Button mLoginButton;
    private SystemSession mSystemSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the system session
        mSystemSession = new SystemSession(getApplicationContext());


        LoginButtonListener();
        InitLogo();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void InitLogo() {
        // Font path
        String fontPath = "fonts/LHANDW.TTF";

        // text view label
        TextView txtGhost = (TextView) findViewById(R.id.text_view_logo);

        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        // Applying font
        txtGhost.setTypeface(tf);
    }

    private void LoginButtonListener() {
        // create click listener

        mLoginButton = (Button) findViewById(R.id.button_login);

        View.OnClickListener mLoginButton_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username, password;

                // Extracting values from the input texts
                username = ((EditText)findViewById(R.id.edit_text_username)).getText().toString();
                password = ((EditText)findViewById(R.id.edit_text_password)).getText().toString();


                String IP_GET_SERVICE_URL = "http://sharon-se-server.dynu.com:9002/IPGetterService/json/dummyuser";
                RequestFactory requestFactory = new RequestFactory(getApplicationContext());

                // TODO: sending login request
                if (username.equals("") && password.equals("")) {
                    mSystemSession.Login();

                    JSONObject body = new JSONObject();
                    // Request IP from server
                    requestFactory.createRequest(Request.Method.GET, IP_GET_SERVICE_URL, body,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    // Extract the IP from the response
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String ip = jsonObject.getJSONObject("GetPCIPJsonResult").getString("IP");

                                        if (ip != null) {
                                            // Save the ip
                                            mSystemSession.setRemoteIPAddress(ip);
                                        } else {
                                            mSystemSession.setRemoteIPAddress(SystemSession.NO_IP);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        mSystemSession.setRemoteIPAddress(SystemSession.NO_IP);
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Error", error.getMessage());
                                    mSystemSession.setRemoteIPAddress(SystemSession.NO_IP);
                                }
                            });

                    LoadFeedActivity();

                } else {

                }
                Log.d("System state login", mSystemSession.getRemoteIPAddress());

            }
        };

        // Assign click listener to the Login button
        mLoginButton.setOnClickListener(mLoginButton_onClick);
    }

    private void LoadFeedActivity() {

        Intent intent;
        intent = new Intent(getApplicationContext(), FeedActivity.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        startActivity(intent);

        // Finish this activity
        finish();
    }


}
