package com.example.orensharon.finalproject.gui.feed.sections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        String jsonData;
        Bundle data;



        view = inflater.inflate(R.layout.fragment_contact_details, container, false);

        data = getArguments();
        jsonData = data.getString("data");

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


        try {
            json = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (json != null) {
            // Set values into the text views
            try {
                displayNameTextView.setText(json.get("DisplayName").toString());

                phonesTextView.setText(JsonArrayToString(json.getJSONArray("Phones"), new String[] {"Number", "Type"} ));
                emailsTextView.setText(JsonArrayToString(json.getJSONArray("Emails"), new String[] {"Address", "Type"} ));
                addressesTextView.setText(JsonArrayToString(json.getJSONArray("Addresses"), new String[] {"Address", "Type"} ));

                if (!json.getJSONObject("Organization").get("Company").toString().equals("null")) {
                    organizationCompanyTextView.setText(json.getJSONObject("Organization").get("Company").toString());
                }

                if (!json.getJSONObject("Organization").get("Title").toString().equals("null")) {
                    organizationTitleTextView.setText(json.getJSONObject("Organization").get("Title").toString());
                }

                if (!json.get("Notes").toString().equals("null")) {
                    notesTextView.setText(json.get("Notes").toString());
                }


            } catch (JSONException e) {
                e.printStackTrace();
                //  feedItems[i] = new FeedContactItem(i, "");
            }



        }

        else {
            //      TODO: show error

        }






        return view;
    }

    private String JsonArrayToString(JSONArray array, String[] params) throws JSONException {

        String result = "";

        for (int i = 0 ; i < array.length(); i ++) {
            JSONObject obj = array.getJSONObject(i);
            result += obj.getString(params[0]) + " (" + obj.getString(params[1]) + ")";
            if (i < array.length() - 1) {
                result += "\n";
            }
        }
        return result;
    }



}
