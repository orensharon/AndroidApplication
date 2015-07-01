package com.example.orensharon.finalproject.service.observers;

import android.content.Context;
import android.net.Uri;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.service.managers.ContactManager;


/**
 * Created by orensharon on 12/13/14.
 * This is the contacts deactivate class from base content observer
 */
public class ContactObserver extends BaseContentObserver {


    public ContactObserver(Context context, Uri uri) {
        super(context);

        // Create a manager for that content. The manager will monitor the change of the content

        mContentType = ApplicationConstants.TYPE_OF_CONTENT_CONTACT;
        mManager = new ContactManager(mContext, uri, mContentType);
        this.Manage();


    }

}
