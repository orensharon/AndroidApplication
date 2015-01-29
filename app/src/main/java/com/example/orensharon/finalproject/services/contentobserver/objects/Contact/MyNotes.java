package com.example.orensharon.finalproject.services.contentobserver.objects.Contact;

/**
 * Created by orensharon on 12/16/14.
 */
public class MyNotes {

    private String mNotes;

    public MyNotes() {
        super();
    }

    public MyNotes(String notes) {
        super();
        this.mNotes = notes;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        this.mNotes = mNotes;
    }

    @Override
    public String toString() {
        return "MyNotes [notes=" + mNotes + "]";
    }

}
