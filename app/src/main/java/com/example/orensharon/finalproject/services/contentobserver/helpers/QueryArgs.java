package com.example.orensharon.finalproject.services.contentobserver.helpers;

import android.net.Uri;

/**
 * Created by orensharon on 12/27/14.
 */
public class QueryArgs {

    private Uri mUri;
    private String[] mProjection;

    private String mSelection;
    private String[] mSelectionArgs;

    private String mSortOrder;

    public QueryArgs(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    public Uri getUri() {
        return mUri;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public String getSelection() {
        return mSelection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public String getSortOrder() {
        return mSortOrder;
    }
}
