package com.example.orensharon.finalproject.gui.feed.sections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 5/23/15.
 * Gui of contact details
 */
public class ContactDetails extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.fragment_contact_details, container, false);

        initView(view);

        return view;
    }

    // Init the contact details screen
    private void initView(View view) {
        Bundle data;
        String jsonData;
        data = getArguments();
        jsonData = data.getString(Contacts.OBJECT_DATA);

        JSONObject json = null;

        TextView displayNameTextView, phonesTextView, emailsTextView, addressesTextView,
                organizationCompanyTextView, organizationTitleTextView, notesTextView;

        displayNameTextView = (TextView) view.findViewById(R.id.contact_name_value_text_view);
        phonesTextView = (TextView) view.findViewById(R.id.contact_phones_value_text_view);
        emailsTextView = (TextView) view.findViewById(R.id.contact_emails_value_text_view);
        addressesTextView = (TextView) view.findViewById(R.id.contact_addresses_value_text_view);
        organizationCompanyTextView = (TextView) view.findViewById(R.id.contact_organization_company_value_text_view);
        organizationTitleTextView = (TextView) view.findViewById(R.id.contact_organization_title_value_text_view);
        notesTextView = (TextView) view.findViewById(R.id.contact_notes_value_text_view);


        // Load contact info

        try {
            json = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (json != null) {
            // Set values into the text views
            try {
                displayNameTextView.setText(json.get(ApplicationConstants.CONTACT_DISPLAY_NAME_KEY).toString());

                phonesTextView.setText(
                        JsonArrayToString(json.getJSONArray(ApplicationConstants.CONTACT_PHONES_KEY),
                                new String[] {
                                        ApplicationConstants.CONTACT_PHONE_NUMBER_KEY,
                                        ApplicationConstants.CONTACT_PHONE_TYPE_KEY
                                }));

                emailsTextView.setText(
                        JsonArrayToString(json.getJSONArray(ApplicationConstants.CONTACT_EMAIL_KEY),
                                new String[] {
                                        ApplicationConstants.CONTACT_ADDRESS_ADDRESS_KEY,
                                        ApplicationConstants.CONTACT_ADDRESS_TYPE_KEY
                                }));

                addressesTextView.setText(
                        JsonArrayToString(json.getJSONArray(ApplicationConstants.CONTACT_ADDRESSES_KEY),
                                new String[] {
                                        ApplicationConstants.CONTACT_ADDRESS_ADDRESS_KEY,
                                        ApplicationConstants.CONTACT_ADDRESS_TYPE_KEY
                                }));

                String orgKey = ApplicationConstants.CONTACT_ORGANIZATION_KEY;
                if (!json.getString(orgKey).equals("null")) {
                    if (!json.getJSONObject(orgKey)
                            .get(ApplicationConstants.CONTACT_ORGANIZATION_COMPANY_KEY).toString().equals("null")) {
                        organizationCompanyTextView.setText(json.getJSONObject(orgKey)
                            .get(ApplicationConstants.CONTACT_ORGANIZATION_COMPANY_KEY).toString());
                    } else {
                        organizationCompanyTextView.setText(R.string.feed_content_no_info);
                    }

                    if (!json.getJSONObject(orgKey)
                            .get(ApplicationConstants.CONTACT_ORGANIZATION_TITLE_KEY).toString().equals("null")) {
                        organizationTitleTextView.setText(json.getJSONObject(orgKey)
                                .get(ApplicationConstants.CONTACT_ORGANIZATION_TITLE_KEY).toString());
                    } else {
                        organizationTitleTextView.setText(R.string.feed_content_no_info);
                    }
                } else {
                    organizationCompanyTextView.setText(R.string.feed_content_no_info);
                    organizationTitleTextView.setText(R.string.feed_content_no_info);
                }
                if (!json.get(ApplicationConstants.CONTACT_NOTES_KEY).toString().equals("null")) {
                    notesTextView.setText(json.get(ApplicationConstants.CONTACT_NOTES_KEY).toString());
                } else {
                    notesTextView.setText(R.string.feed_content_no_info);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    // Create String from given json array
    private String JsonArrayToString(JSONArray array, String[] params) throws JSONException {

        String result = "";

        for (int i = 0 ; i < array.length(); i ++) {
            JSONObject obj = array.getJSONObject(i);
            int typeId = obj.getInt(params[1]);
            String type = "";

            if (params[0].equals(ApplicationConstants.CONTACT_PHONE_NUMBER_KEY)) {
                // Means phones
                type = ApplicationConstants.CONTACT_PHONE_TYPES[typeId];
            } else if (params[0].equals(ApplicationConstants.CONTACT_ADDRESS_ADDRESS_KEY)) {
                // Means Emails / Addresses
                type = ApplicationConstants.CONTACT_ADDRESSES_TYPES[typeId];
            }

            result += type + ":\t\t\t\t" + obj.getString(params[0]);
            if (i < array.length() - 1) {
                result += "\n";
            }
        }

        if (result.equals("")) {
            result = getResources().getString(R.string.feed_content_no_info);
        }
        return result;
    }



}
