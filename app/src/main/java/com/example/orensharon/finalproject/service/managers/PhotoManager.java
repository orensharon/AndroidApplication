package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.example.orensharon.finalproject.service.objects.BaseObject;
import com.example.orensharon.finalproject.service.objects.Photo.MyPhoto;
import com.example.orensharon.finalproject.utils.MD5Checksum;

import java.io.File;
/**
 * Created by orensharon on 12/17/14.
 * This is the photo module of the content manager
 */
public class PhotoManager extends BaseManager {

    public PhotoManager (Context context, Uri uri, String contentType) {
        super(context, uri, contentType);


    }

    @Override
    public BaseObject getContent(Cursor cursor) {
        MyPhoto photo;
        String checksum;

        photo = null;

        // Reading from device content database according to the given uri
        if (cursor != null) {

            String filePath, mimeType, title, id;

            id = getColumnString(cursor, MediaStore.MediaColumns._ID);
            filePath = getColumnString(cursor, MediaStore.MediaColumns.DATA);
            mimeType = getColumnString(cursor, MediaStore.MediaColumns.MIME_TYPE);
            title = getColumnString(cursor, MediaStore.MediaColumns.TITLE);

            checksum = MD5Checksum.getMd5HashFromFilePath(filePath);

            // Creating new Photo object with extracted data
            photo = new MyPhoto(Integer.parseInt(id), ApplicationConstants.TYPE_OF_CONTENT_PHOTO, new File(filePath), mimeType, title,checksum);
        }
        return photo;
    }

    @Override
    public BaseObject getBaseContent(Cursor cursor) {

        int id;
        String checksum, filePath;
        BaseObject result;

        id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));



        filePath = getColumnString(cursor, MediaStore.MediaColumns.DATA);
        checksum = MD5Checksum.getMd5HashFromFilePath(filePath);


        // Create new contact with matched id and string of its name
        result = new BaseObject(id, ApplicationConstants.TYPE_OF_CONTENT_PHOTO, checksum);
        return result;

    }


}
