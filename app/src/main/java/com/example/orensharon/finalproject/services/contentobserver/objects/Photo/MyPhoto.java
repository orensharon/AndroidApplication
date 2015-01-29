package com.example.orensharon.finalproject.services.contentobserver.objects.Photo;

import com.example.orensharon.finalproject.services.contentobserver.objects.BaseObject;

import java.io.File;

/**
 * Created by orensharon on 12/27/14.
 */
public class MyPhoto extends BaseObject {

    private File mFile;
    private String mTitle;

    @SuppressWarnings("unused")
    private String mType;

    public MyPhoto(String id, File file, String type, String title) {

        super(id);
        mFile = file;
        mType = type;
        mTitle = title;
    }



    public String getType() {
        return mType;
    }

    public File getFile() {
        return mFile;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public String toString() {
        return "MyPhoto [id=" + getId() + ", title=" + mTitle
                + ", file=" + mFile.toString() + ", type=" + mType + "]";
    }
}
