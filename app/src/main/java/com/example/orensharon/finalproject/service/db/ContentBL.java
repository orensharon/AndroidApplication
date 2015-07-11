package com.example.orensharon.finalproject.service.db;

import android.content.Context;

import com.example.orensharon.finalproject.service.objects.BaseObject;

import java.util.List;

/**
 * Created by orensharon on 5/7/15.
 * Content database business logic
 */
public class ContentBL {

    private ContentDAL mContentDAL;

    public ContentBL(Context context) {

        mContentDAL = new ContentDAL(context);
    }

    // Insert new content to replica
    public void InsertContent(BaseObject baseObject) {
        mContentDAL.InsertContent(baseObject.getTypeOfContent(),
                baseObject.getId(), baseObject.getChecksum());
    }

    // Delete content from reploca
    public void DeleteContent(String type, int id) {
        mContentDAL.DeleteContent(type, id);
    }

    // Cancel all in sync in replica
    public void CancelAllInSync(String type) {
        mContentDAL.CancelAllInSync(type);
    }

    // Flags setters
    public void setInSync(int id, boolean flag, String type) {
        mContentDAL.setInSync(id, flag, type);
    }

    public void setReturnedError(int id, boolean flag, String type) {
        mContentDAL.setReturnedError(id, flag, type);
    }

    public void setSynced(int id, boolean flag, String type) {
        mContentDAL.setSynced(id, flag, type);
    }

    public void setDirty(int id, boolean flag, String type) {
        mContentDAL.setDirty(id, flag, type);
    }

    // Other setters
    public void setChecksum(int id, String checksum, String type) {
        mContentDAL.setChecksum(id, checksum, type);
    }

    public void setDateModified(int id, long timeStamp, String type) {
        mContentDAL.setDateModified(id, timeStamp, type);
    }


    // Getters by id
    public boolean getDirty(int id, String type) {
        return mContentDAL.getDirty(id,type);
    }

    public DBContent getById(int id,String type) {
        return mContentDAL.getById(id, type);
    }

    public DBContent getNextInSync(String type) {
        return mContentDAL.getNextToSync(type);
    }

    public int getLastIDByContentType(String type) {
        return mContentDAL.getLastIDByContentType(type);
    }

    public DBContent isContentExist(String type, int id) {
        return mContentDAL.getContentIfExist(type, id);
    }


    // List getters by flag
    public List<DBContent> getAllContents(String type) {
        return mContentDAL.getAllContents(type);
    }

    public List<DBContent> getAllUnsyncedContents(String type) {
        return mContentDAL.getAllUnsyncedContents(type);
    }

    public List<Integer> getAllErrorContents(String type) {
        return mContentDAL.getAllErrorContents(type);
    }

    public List<Integer> getAllInSyncContents(String type) {
        return mContentDAL.getAllInSyncContents(type);
    }

    public List<Integer> getAllSyncedContents(String type) {
        return mContentDAL.getAllSyncedContents(type);
    }


}
