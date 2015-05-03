package com.example.orensharon.finalproject.gui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.logic.RequestFactory;
import com.example.orensharon.finalproject.service.managers.BaseManager;
import com.example.orensharon.finalproject.service.managers.ContactManager;
import com.example.orensharon.finalproject.service.objects.Contact.MyContact;
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

                // TODO: Form validation
                if (!username.equals("") && !password.equals("")) {


                    // Sending login request to server
                    LoginAttempt(username, password);

                } else {
                    // Show message
                }


            }
        };

        // Assign click listener to the Login button
        mLoginButton.setOnClickListener(mLoginButton_onClick);
    }

    private void LoginAttempt(String username, String password) {

        JSONObject body;
        RequestFactory requestFactory;

        requestFactory = new RequestFactory(getApplicationContext());


        body = new JSONObject();
        try {
            // Creating json object from username and password
            // Will be the login request body
            body.put(ApplicationConstants.LOGIN_USERNAME_KEY, username);
            body.put(ApplicationConstants.LOGIN_PASSWORD_KEY, password);
        } catch (JSONException e) {
            e.printStackTrace();
            body = null;
        }

        // Make sure the json object was successfully created
        if (body != null) {

            requestFactory.createJsonRequest(
                    Request.Method.POST,
                    ApplicationConstants.LOGIN_API,
                    body.toString(),
                    null,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // handle the login response

                            String token = null;

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                token = jsonObject.getString(ApplicationConstants.AUTH_TOKEN_KEY);

                            } catch (JSONException e) {
                                // Server issues
                                e.printStackTrace();
                            }

                            if (token != null) {

                                // Set login state
                                mSystemSession.Login(token);

                                // Request safe IP from server
                                RequestSafeIP(token);



                                LoadFeedActivity();
                            } else {
                                // Something is wrong with reading the token
                                // Unexpected response

                                Toast.makeText(getApplicationContext(), "Unexpected Login response (Not error)",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        private void RequestSafeIP(final String token) {

                            // After login - create a request to server to get the ip address of the safe



                            RequestFactory requestFactory = new RequestFactory(getApplicationContext());
                            JSONObject body = new JSONObject();

                            requestFactory.createJsonRequest(
                                    Request.Method.GET,
                                    ApplicationConstants.IP_GET_API, body.toString(), token,
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
                                            // empty string for - server don't know the ip
                                            if (ip != null && !ip.equals("")) {

                                                // Save the ip
                                                mSystemSession.setIPAddressOfSafe(ip);

                                            } else {
                                                // TODO: fix no-ip issue

                                                RequestSafeIP(token);
                                                mSystemSession.setIPAddressOfSafe(ApplicationConstants.NO_IP_VALUE);

                                            }

                                            Toast.makeText(getApplicationContext(), mSystemSession.geIPAddressOfSafe(),
                                                    Toast.LENGTH_LONG).show();
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


                                            if (errorMessage != null ){
                                                Toast.makeText(getApplicationContext(), errorMessage,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
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
                                        errorMessage = "Username or password are incorrect.\nPlease try again";
                                        break;

                                    // 403
                                    case ApplicationConstants.HTTP_FORBIDDEN:
                                        errorMessage = "Forbidden request";
                                        break;


                                }

                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();
                            }


                            if (errorMessage != null ){
                                Toast.makeText(getApplicationContext(), errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            );
        }


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


    public void Testing(View view) {
/*
        // Contact Uploading test
        BaseManager contactManager = new ContactManager(getApplicationContext(), ContactsContract.Contacts.CONTENT_URI);
        MyContact myContact = (MyContact)contactManager.requestLatestContentFromDatabase();

        Log.e("Contact",myContact.toJSONObject().toString());


        JSONObject body;
        RequestFactory requestFactory;

        String ip = mSystemSession.geIPAddressOfSafe();
        String url = "http://" + ip + ApplicationConstants.CONTACT_UPLOAD_API;

        requestFactory = new RequestFactory(getApplicationContext());


--------
        // Photo Uploading Test

        File file = new File("/storage/sdcard0/DCIM/Camera/20150418_234415.jpg");
        final MyPhoto photo = new MyPhoto("505", "MyPhoto", file,"image/jpeg", "20150418_234415");
        String ip = mSystemSession.geIPAddressOfSafe();

        String url = "http://" + ip + ApplicationConstants.UPLOAD_STREAM_API_SUFFIX;
        RequestFactory requestFactory = new RequestFactory(getApplication());


        */
    }
}
