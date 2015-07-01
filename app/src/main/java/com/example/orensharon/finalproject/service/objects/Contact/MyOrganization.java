package com.example.orensharon.finalproject.service.objects.Contact;

import com.example.orensharon.finalproject.ApplicationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by orensharon on 12/15/14.
 */
public class MyOrganization {

    private String company;
    private String title;

    public MyOrganization() {
        super();
    }

    public MyOrganization(String company, String title) {
        super();
        this.company = company;
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MyOrganization [company=" + company + ", title=" + title + "]";
    }

    public JSONObject toJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ApplicationConstants.CONTACT_ORGANIZATION_COMPANY_KEY,company);
            jsonObject.put(ApplicationConstants.CONTACT_ORGANIZATION_TITLE_KEY,title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }
}
