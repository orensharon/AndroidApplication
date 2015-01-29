package com.example.orensharon.finalproject.services.contentobserver.observers;

import android.content.Context;
import android.net.Uri;

import com.example.orensharon.finalproject.services.contentobserver.managers.ContactManager;


/**
 * Created by orensharon on 12/13/14.
 * This is the contacts deactivate class from base content observer
 */
public class ContactObserver extends BaseContentObserver {


    public ContactObserver(Context context, Uri uri) {
        super(context);

        // Create a manager for that content. The manager will monitor the change of the content

        mManager = new ContactManager(mContext, uri);
        this.Manage();

        // Get the last contact
        /*Object obj = mManager.requestLastContentById(MyContact.class);
        MyContact contact = MyContact.class.cast(obj);
        Log.e("last contact", contact.toString());*/


        // Get all the contacts
        /*Object obj = mManager.requestAllContent(MyContact.class);
        List<MyContact> contacts = (List<MyContact>) obj;
        Log.e("all contacts", contacts.toString());*/


    }

}
