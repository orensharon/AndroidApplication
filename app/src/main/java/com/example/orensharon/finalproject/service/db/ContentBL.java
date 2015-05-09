package com.example.orensharon.finalproject.service.db;

import android.content.Context;

import com.example.orensharon.finalproject.service.objects.BaseObject;

import java.util.List;

/**
 * Created by orensharon on 5/7/15.
 * Content database business logic
 */
public class ContentBL {

    private ContentDAL mHistoryDAL;

    public ContentBL(Context context) {

        mHistoryDAL = new ContentDAL(context);
    }

    public void InsertContent(BaseObject baseObject) {
        mHistoryDAL.InsertContent(baseObject.getTypeOfContent(), baseObject.getId(), baseObject.getChecksum());
    }

    public void DeleteContent(String type, int id) {
        mHistoryDAL.DeleteContent(type, id);
    }

    public void CancelAllInSync(String type) {
        mHistoryDAL.CancelAllInSync(type);
    }

    public void setInSync(int id, boolean flag, String type) {
        mHistoryDAL.setInSync(id, flag, type);
    }



    public void setChecksum(int id, String checksum, String type) {
        mHistoryDAL.setChecksum(id, checksum, type);
    }
    public void setReturnedError(int id, boolean flag, String type) {
        mHistoryDAL.setReturnedError(id, flag, type);
    }

    public void setSynced(int id, boolean flag, String type) {
        mHistoryDAL.setSynced(id, flag, type);
    }

    public DBContent getNextInSync(String type) {
        return mHistoryDAL.getNextToSync(type);
    }

    public int getLastIDByContentType(String type) {
        return mHistoryDAL.getLastIDByContentType(type);
    }


    public DBContent isContentExist(String type, int id) {
        return mHistoryDAL.getContentIfExist(type, id);
    }


    public List<DBContent> getAllContents(String type) {
        return mHistoryDAL.getAllContents(type);
    }

    public List<DBContent> getAllUnsyncedContents(String type) {
        return mHistoryDAL.getAllUnsyncedContents(type);
    }
    /*
    public List<Integer> getAllErrorContents(String type) {
        return mHistoryDAL.getAllErrorContents(type);
    }

    public List<Integer> getAllInSyncContents(String type) {
        return mHistoryDAL.getAllInSyncContents(type);
    }

    public List<Integer> getAllSyncedContents(String type) {
        return mHistoryDAL.getAllSyncedContents(type);
    }

    */
}
