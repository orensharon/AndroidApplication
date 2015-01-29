package com.example.orensharon.finalproject.service.managers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.orensharon.finalproject.service.objects.Photo.MyPhoto;

import java.io.File;
/**
 * Created by orensharon on 12/17/14.
 * This is the photo module of the content manager
 */
public class PhotoManager extends BaseManager {

    public PhotoManager (Context context, Uri uri) {
        super(context, uri);
    }

    @Override
    public Object getContent(Cursor cursor) {
        MyPhoto photo;

        photo = null;

        // Reading from device content database according to the given uri
        if (cursor != null) {

            String filePath, mimeType, title, id;

            id = getColumnString(cursor, MediaStore.MediaColumns._ID);
            filePath = getColumnString(cursor, MediaStore.MediaColumns.DATA);
            mimeType = getColumnString(cursor, MediaStore.MediaColumns.MIME_TYPE);
            title = getColumnString(cursor, MediaStore.MediaColumns.TITLE);


            // Creating new contentItem with the data
            photo = new MyPhoto(id, new File(filePath), mimeType, title);
        }
        return photo;
    }


}
