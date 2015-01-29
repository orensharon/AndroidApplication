package com.example.orensharon.finalproject.services.contentobserver.objects.Contact;

import android.net.Uri;

import com.example.orensharon.finalproject.services.contentobserver.objects.BaseObject;

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

    public MyContact(String id) {
        super(id);
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


}
