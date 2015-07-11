package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.service.helpers.QueryArgs;
import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.service.objects.Contact.MyAddress;
import com.example.orensharon.finalproject.service.objects.Contact.MyContact;
import com.example.orensharon.finalproject.service.objects.Contact.MyEmail;
import com.example.orensharon.finalproject.service.objects.Contact.MyNotes;
import com.example.orensharon.finalproject.service.objects.Contact.MyOrganization;
import com.example.orensharon.finalproject.service.objects.Contact.MyPhone;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by orensharon on 12/15/14.
 * * This is the contacts deactivate class from base content manager
 */
public class ContactManager extends BaseManager {


    public ContactManager(Context context, Uri uri, String contentType) {

        super(context, uri, contentType);


    }

    /* Used by the base manager class */
    @Override
    public BaseObject getContent(Cursor cursor) {
        // From a given cursor - create a new MyContact and return it

        int id;
        String name, version;
        MyContact contact;

        id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        // The version is the checksum in the case of contacts
        version = getVersion(id);

        // Create new contact with matched id and string of its name
        contact = new MyContact(id, ApplicationConstants.TYPE_OF_CONTENT_CONTACT, version);
        contact.setDisplayName(name);

        // Check if there is a phone number exist for this user
        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

            // There is phones exist for the specific contact
            contact.setPhones( requestPhoneNumbers(contact.getId()));
        }

        // Adding all other information
        contact.setEmails( requestEmails(contact.getId()) );
        contact.setAddresses(requestAddresses(contact.getId()));
        contact.setOrganization( requestOrganization(contact.getId()) );
        contact.setNotes( requestNotes(contact.getId()) );
        contact.setPhoto( getDataUri(contact.getId()) );

        return contact;
    }

    @Override
    public BaseObject getBaseContent(Cursor cursor) {
        int id;
        String version;
        BaseObject result;

        id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));


        // The version is the checksum in the case of contacts
        version = getVersion(id);

        // Create new contact with matched id and string of its name
        result = new BaseObject(id, ApplicationConstants.TYPE_OF_CONTENT_CONTACT, version);
        return result;

    }

    /* Used for internal usage - building the contact profile */
    private List<MyPhone> requestPhoneNumbers(int idContact) {

        // Request and return a list of the phone numbers of the given contact id

        Uri uri;
        List<MyPhone> phones;
        Cursor cursor;
        String number, type;
        MyPhone phone;

        QueryArgs queryArgs;
        String selection;
        String[] selectionArgs;

        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;



        // Getting all the phones of given contact id

        selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        selectionArgs = new String[]{ String.valueOf(idContact) };

        queryArgs = new QueryArgs(uri, null, selection, selectionArgs, null);
        cursor = getCursor(queryArgs);

        if (cursor == null) {
            // Cursor reading error
            return null;
        }


        phones = new ArrayList<MyPhone>();
        while (cursor.moveToNext()) {

            // Iterate over all the phones of the contact id
            number = getColumnString(cursor, ContactsContract.CommonDataKinds.Phone.NUMBER);
            type = getColumnString(cursor, ContactsContract.CommonDataKinds.Phone.TYPE);

            // Create phone object and add it to list
            phone = new MyPhone(number, type);
            phones.add(phone);
        }

        cursor.close();

        return phones;
    }
    private List<MyEmail> requestEmails(int idContact) {

        // Request and return a list of the email addresses of the given contact id

        Cursor cursor;
        String emailAddress, emailType;
        String selection;
        String[] selectionArgs;
        Uri uri;
        List<MyEmail> emails;
        MyEmail email;
        QueryArgs queryArgs;

        selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
        selectionArgs = new String[]{String.valueOf(idContact) };

        uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

        queryArgs = new QueryArgs(uri, null, selection, selectionArgs, null);
        cursor = getCursor(queryArgs);

        if (cursor == null) {
            // Cursor reading error
            return null;
        }

        emails = new ArrayList<MyEmail>();

        while (cursor.moveToNext()) {

            // Iterate over all the emails of the contact id

            emailAddress = getColumnString(cursor, ContactsContract.CommonDataKinds.Email.DATA);
            emailType = getColumnString(cursor, ContactsContract.CommonDataKinds.Email.TYPE);

            // Create email object and add it to list
            email = new MyEmail(emailAddress,emailType);
            emails.add(email);
        }

        cursor.close();

        return emails;
    }
    private List<MyAddress> requestAddresses(int idContact) {

        String selection;
        //String poBox, street, city, state, postalCode, country,
        String type;
        String formattedAddress;

        String[] selectionArgs;
        Cursor cursor;
        List<MyAddress> addresses;
        MyAddress address;
        Uri uri;
        QueryArgs queryArgs;

        selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        selectionArgs = new String[]{String.valueOf(idContact),
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

        uri = ContactsContract.Data.CONTENT_URI;

        queryArgs = new QueryArgs(uri, null, selection, selectionArgs, null);
        cursor = getCursor(queryArgs);

        if (cursor == null) {
            // Cursor reading error
            return null;
        }

        addresses = new ArrayList<MyAddress>();

        while(cursor.moveToNext()) {

            type = getColumnString(cursor, ContactsContract.CommonDataKinds.StructuredPostal.TYPE);


            formattedAddress = getColumnString(cursor,
                    ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);

            address = new MyAddress(formattedAddress, type);
            addresses.add(address);
        }
        cursor.close();

        return addresses;
    }
    private MyOrganization requestOrganization(int idContact) {

        String selection;
        String[] selectionArgs;
        MyOrganization organization;
        String orgName, title;
        Uri uri;
        Cursor cursor;

        organization = new MyOrganization();

        selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        selectionArgs = new String[]{String.valueOf(idContact),
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

        uri = ContactsContract.Data.CONTENT_URI;

        cursor = mContext.getContentResolver().query(
                uri,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor == null) {
            // Cursor reading error
            return null;
        }

        if (cursor.moveToFirst()) {
            orgName = getColumnString(cursor, ContactsContract.CommonDataKinds.Organization.DATA);
            title = getColumnString(cursor, ContactsContract.CommonDataKinds.Organization.TITLE);

            organization.setCompany(orgName);
            organization.setTitle(title);
        }

        cursor.close();
        return organization;
    }
    private MyNotes requestNotes(int idContact) {

        String selection;
        String[] selectionArgs;
        Cursor cursor;
        Uri uri;
        MyNotes notes;
        QueryArgs queryArgs;

        notes = null;

        uri = ContactsContract.Data.CONTENT_URI;
        selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        selectionArgs = new String[]{String.valueOf(idContact),
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

        queryArgs = new QueryArgs(uri, null, selection, selectionArgs, null);
        cursor = getCursor(queryArgs);

        if (cursor == null) {
            // Cursor reading error
            return  null;
        }

        if (cursor.moveToFirst()) {
            String note;
            note = getColumnString(cursor, ContactsContract.CommonDataKinds.Note.NOTE);
            notes = new MyNotes(note);
        }
        cursor.close();

        return notes;
    }


    //Available only for API level 11+
    private Uri getPhotoThumbnailUri(int idContact, Uri contactUri){

        Cursor cursor;
        String[] projection;
        QueryArgs queryArgs;

        projection = new String[] { ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };

        queryArgs = new QueryArgs(contactUri, projection, null, null, null);
        cursor = getCursor(queryArgs);

        if (cursor == null){
            // Cursor reading error
            return Uri.EMPTY;
        }

        // Make sure this specific contact has image by checking if the PHOTO_ID column is not null
        if (cursor.moveToFirst() &&
                !cursor.isNull(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))){

            // return the uri of the cursor
            cursor.close();
            return Uri.parse(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
        }

        // No records found
        cursor.close();
        return Uri.EMPTY;
    }
    private int getDataUri(int idContact) {

        // From given id of contact - get the data Uri and return it

        Cursor cursor;
        Uri contactUri;
        String[] projection;
        QueryArgs queryArgs;

        contactUri = Uri.withAppendedPath(getObservingUri(), String.valueOf(idContact) );
        projection = new String[] { ContactsContract.Contacts.PHOTO_ID };

        queryArgs = new QueryArgs(contactUri, projection, null, null, null);
        cursor = getCursor(queryArgs);

        if (cursor == null){
            // Cursor reading error
            return 0;
        }


        int id = 0;
        // Make sure this specific contact has image by checking if the PHOTO_ID column is not null
        if (cursor.moveToFirst()
                && !cursor.isNull(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID))){


            id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));

            /**http://developer.android.com/reference/android/provider/ContactsContract.ContactsColumns.html#PHOTO_ID
             * If PHOTO_ID is null, consult PHOTO_URI or PHOTO_THUMBNAIL_URI,
             * which is a more generic mechanism for referencing the contact photo,
             * especially for contacts returned by non-local directories (see ContactsContract.Directory).
             */

        }

        cursor.close();
        return id;
    }


    private String getVersion(int id) {

        String result = null;
        //specify which fields of RawContacts table to be retrieved.
        String[] projection = new String[]{ContactsContract.RawContacts.VERSION};
        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{ String.valueOf(id) };
        //query the RawContacts.CONTENT_URI
        Cursor cur = mContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);

        if (cur == null) {
            return null;
        }
        if (cur.moveToNext()) {

            result = cur.getString(cur.getColumnIndex(ContactsContract.RawContacts.VERSION));
            cur.close();
        }

        return result;
    }


}
