package com.example.orensharon.finalproject.gui.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
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
import com.example.orensharon.finalproject.sessions.SettingsSession;
import com.example.orensharon.finalproject.sessions.SystemSession;
import com.example.orensharon.finalproject.utils.IPAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private SystemSession mSystemSession;

    private ProgressDialog mProgressDialog;

    private EditText mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the system session
        mSystemSession = new SystemSession(getApplicationContext());

        LoginButtonListener();
        InitLogo();

        // Read save username and password from the session
        // And set them in the text fields
        mUsername = ((EditText)findViewById(R.id.edit_text_username));
        mPassword = ((EditText)findViewById(R.id.edit_text_password));

        mUsername.setText(mSystemSession.getUsername());
        mPassword.setText(mSystemSession.getPassword());

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

    private void initProgressDialog() {

        // Init the progress bar
        mProgressDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        mProgressDialog.setMessage("Please wait...");
        // Set Cancelable as False
        mProgressDialog.setCancelable(false);
    }

    private void LoginButtonListener() {
        // create click listener

        Button loginButton = (Button) findViewById(R.id.button_login);

        View.OnClickListener mLoginButton_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String username, password;

                // Extracting values from the input texts
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                // TODO: Form validation
                if (!username.equals("") && !password.equals("")) {

                    initProgressDialog();
                    // Show the progress bar
                    mProgressDialog.show();

                    // Sending login request to server
                    LoginAttempt(username, password);

                } else {
                    // Show message
                    ShowErrorDialog("Please fill all the required fields");
                }


            }
        };

        // Assign click listener to the Login button
        loginButton.setOnClickListener(mLoginButton_onClick);
    }

    private void LoginAttempt(final String username, final String password) {

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



                            // Hide Progress Dialog
                            mProgressDialog.hide();
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }

                            HandleLoginResponse(response);
                        }

                        private void HandleLoginResponse(String response) {
                            String token = null;

                            // TODO: read token from header
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                token = jsonObject.getString(ApplicationConstants.AUTH_TOKEN_KEY);

                            } catch (JSONException e) {
                                // Server issues
                                e.printStackTrace();
                            }

                            if (token != null) {

                                // Set login state
                                mSystemSession.Login(token, username, password);

                                // Request safe IP from server
                               // RequestSafeIP(token);

                                LoadFeedActivity();
                            } else {
                                // Something is wrong with reading the token
                                // Unexpected response
                                ShowErrorDialog("Unexpected Login response (Not error)");
                                /*Toast.makeText(getApplicationContext(), "Unexpected Login response (Not error)",
                                        Toast.LENGTH_LONG).show();*/
                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String errorMessage = null;

                            // Hide Progress Dialog
                            mProgressDialog.hide();
                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }

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

                            ShowErrorDialog(errorMessage);
                            /*if (errorMessage != null ){
                                Toast.makeText(getApplicationContext(), errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }*/
                        }
                    }
            );
        }


    }

    private void ShowErrorDialog(String message) {

        // Custom 'sorry' dialog with a return button

        final Dialog dialog;

        dialog = new Dialog(this);

        // Set the dialog style - without a title bar
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Set the dialog background to transparent
        //dialog.getWindow().setBackgroundDrawable(
        //        new ColorDrawable(Color.TRANSPARENT));

        // Get the dialog layout
        dialog.setContentView(this.getLayoutInflater().inflate(R.layout.dialog_login_error, null));

        // Set the custom dialog component button
        Button dialogButton = (Button) dialog.findViewById(R.id.button_try_again);

        // Set the error message
        TextView errorMessage = (TextView) dialog.findViewById(R.id.error_message_text_view);

        errorMessage.setText(message);

        // If button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Button clicked - close the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
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
        String url = "http://" + ip + ApplicationConstants.CONTACT_UPLOAD_API_SUFFIX;

        requestFactory = new RequestFactory(getApplicationContext());


--------
        // Photo Uploading Test

        File file = new File("/storage/sdcard0/DCIM/Camera/20150418_234415.jpg");
        final MyPhoto photo = new MyPhoto("505", "MyPhoto", file,"image/jpeg", "20150418_234415");
        String ip = mSystemSession.geIPAddressOfSafe();

        String url = "http://" + ip + ApplicationConstants.PHOTO_UPLOAD_STREAM_API_SUFFIX;
        RequestFactory requestFactory = new RequestFactory(getApplication());


        */
    }
}
