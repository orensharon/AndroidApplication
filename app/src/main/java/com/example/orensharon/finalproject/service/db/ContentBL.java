package com.example.orensharon.finalproject.service.db;

import android.content.Context;

/**
 * Created by orensharon on 5/7/15.
 */
public class ContentBL {

    private ContentDAL mHistoryDAL;

    public ContentBL(Context context) {

        mHistoryDAL = new ContentDAL(context);
    }

    public void InsertContent(String type, String id) {
        mHistoryDAL.InsertContent(type, id);
    }

    public void setInSync(String id, boolean flag) {
        mHistoryDAL.setInSync(id, flag);
    }

    public void setReturnedError(String id, boolean flag) {
        mHistoryDAL.setReturnedError(id, flag);
    }

    public void setSynced(String id, boolean flag) {
        mHistoryDAL.setSynced(id, flag);
    }

    public String getNextInSync() {
        return mHistoryDAL.getNextInSync();
    }
}
