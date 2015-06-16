package com.example.orensharon.finalproject.service.db;

import android.content.Context;

import com.example.orensharon.finalproject.service.objects.BaseObject;

import java.util.List;

/**
 * Created by orensharon on 5/7/15.
 * Content database business logic
 */
public class ContentBL {

    private ContentDAL mContentBL;

    public ContentBL(Context context) {

        mContentBL = new ContentDAL(context);
    }

    public void InsertContent(BaseObject baseObject) {
        mContentBL.InsertContent(baseObject.getTypeOfContent(),
                baseObject.getId(), baseObject.getChecksum());
    }

    public void DeleteContent(String type, int id) {
        mContentBL.DeleteContent(type, id);
    }

    public void CancelAllInSync(String type) {
        mContentBL.CancelAllInSync(type);
    }

    public void setInSync(int id, boolean flag, String type) {
        mContentBL.setInSync(id, flag, type);
    }



    public void setChecksum(int id, String checksum, String type) {
        mContentBL.setChecksum(id, checksum, type);
    }
    public void setReturnedError(int id, boolean flag, String type) {
        mContentBL.setReturnedError(id, flag, type);
    }

    public void setSynced(int id, boolean flag, String type) {
        mContentBL.setSynced(id, flag, type);
    }

    public void setDateModified(int id, long timeStamp, String type) {
        mContentBL.setDateModified(id, timeStamp, type);
    }

    public void setDirty(int id, boolean flag, String type) {
        mContentBL.setDirty(id, flag, type);
    }

    public boolean getDirty(int id, String type) {
        return mContentBL.getDirty(id,type);
    }

    public DBContent getById(int id,String type) {
        return mContentBL.getById(id, type);
    }
    public DBContent getNextInSync(String type) {
        return mContentBL.getNextToSync(type);
    }

    public int getLastIDByContentType(String type) {
        return mContentBL.getLastIDByContentType(type);
    }


    public DBContent isContentExist(String type, int id) {
        return mContentBL.getContentIfExist(type, id);
    }


    public List<DBContent> getAllContents(String type) {
        return mContentBL.getAllContents(type);
    }

    public List<DBContent> getAllUnsyncedContents(String type) {
        return mContentBL.getAllUnsyncedContents(type);
    }
    /*
    public List<Integer> getAllErrorContents(String type) {
        return mContentBL.getAllErrorContents(type);
    }

    public List<Integer> getAllInSyncContents(String type) {
        return mContentBL.getAllInSyncContents(type);
    }

    public List<Integer> getAllSyncedContents(String type) {
        return mContentBL.getAllSyncedContents(type);
    }

    */
}
