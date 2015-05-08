package com.example.orensharon.finalproject.service.objects.Contact;

import android.net.Uri;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.service.objects.BaseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by orensharon on 12/15/14.
 */
public class MyContact extends BaseObject {


    private String mDisplayName;
    private List<MyPhone> mPhones;
    private List<MyEmail> mEmails;
    private List<MyAddress> mAddresses;
    private List<MyInstantMessenger> mInstantMessengers;
    private MyOrganization mOrganization;
    private MyNotes mNotes;
    private Uri mPhotoUri;




    public MyContact(int id, String objectType, String checksum) {
        super(id, objectType, checksum);
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String name) {
        this.mDisplayName = name;
    }

    public MyNotes getNotes() {
        return mNotes;
    }

    public void setNotes(MyNotes notes) {
        this.mNotes = notes;
    }

    public List<MyPhone> getPhones() {
        return mPhones;
    }

    public void setPhones(List<MyPhone> phones) {
        this.mPhones = phones;
    }

    public List<MyEmail> getEmails() {
        return mEmails;
    }

    public void setEmails(List<MyEmail> emails) {
        this.mEmails = emails;
    }

    public List<MyAddress> getAddresses() {
        return mAddresses;
    }

    public void setAddresses(List<MyAddress> addresses) {
        this.mAddresses = addresses;
    }

    public List<MyInstantMessenger> getInstantMessengers() {
        return mInstantMessengers;
    }

    public void setInstantMessengers(List<MyInstantMessenger> instantMessengers) {
        this.mInstantMessengers = instantMessengers;
    }

    public MyOrganization getOrganization() {
        return mOrganization;
    }

    public void setOrganization(MyOrganization organization) {
        this.mOrganization = organization;
    }

    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhoto(Uri photo) {
        this.mPhotoUri = photo;
    }



    @Override
    public String toString() {
        return "MyContact [id=" + getId() + ", display name=" + mDisplayName
                + ", photo uri=" + mPhotoUri.toString() + ", phones=" + mPhones
                + ", emails=" + mEmails + ", addresses=" + mAddresses
                + ", instantMessengers=" + mInstantMessengers
                + ", organization=" + mOrganization + ", notes=" + mNotes + "]";
    }

    public JSONObject toJSONObject() {

        // From class members building JSON object and returns it

        JSONObject jsonObject = new JSONObject();
        // TODO: enum
        try {
            jsonObject.put(ApplicationConstants.CONTACT_ID_KEY, getId());
            jsonObject.put(ApplicationConstants.CONTACT_DISPLAY_NAME_KEY, mDisplayName);
            jsonObject.put(ApplicationConstants.CONTACT_PHOTO_URI_KEY, mPhotoUri.toString());

            // Add list of phones
            JSONArray phones = new JSONArray();

            if (mPhones != null) {
                for (MyPhone phone : mPhones) {
                    JSONObject singlePhone = new JSONObject(phone.toJSONString());
                    phones.put(singlePhone);
                }
            }
            jsonObject.put(ApplicationConstants.CONTACT_PHONES_KEY, phones);

            // Add list of emails
            JSONArray emails = new JSONArray();

            if (mEmails != null) {
                for (MyEmail email : mEmails) {
                    JSONObject singleEmail = new JSONObject(email.toJSONString());
                    emails.put(singleEmail);
                }
            }
            jsonObject.put(ApplicationConstants.CONTACT_EMAIL_KEY, emails);


            // Add list of addresses
            JSONArray addresses = new JSONArray();

            if (mAddresses != null){
                for (MyAddress address : mAddresses) {
                    JSONObject singleAddress = new JSONObject(address.toJSONString());
                    addresses.put(singleAddress);
                }
            }
            jsonObject.put(ApplicationConstants.CONTACT_ADDRESSES_KEY, addresses);

            // Add list of instant messengers
            JSONArray instantMessengers = new JSONArray();

            if (mInstantMessengers != null) {
                for (MyInstantMessenger im : mInstantMessengers) {
                    JSONObject singleIM = new JSONObject(im.toJSONString());
                    instantMessengers.put(singleIM);
                }
            }
            jsonObject.put(ApplicationConstants.CONTACT_INSTANT_MESSENGERS_KEY, instantMessengers);

            if (mOrganization != null) {
                jsonObject.put(ApplicationConstants.CONTACT_ORGANIZATION_KEY, mOrganization.toJSONObject());
            }

            if (mNotes != null) {
                jsonObject.put(ApplicationConstants.CONTACT_NOTES_KEY, mNotes.toJSONString());
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            // TODO: means error building json object from data from db
            return null;
        }
    }


}
