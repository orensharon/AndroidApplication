package com.example.orensharon.finalproject.service.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orensharon on 5/7/15.
 * Content database link class
 */
public class ContentDAL extends SQLiteOpenHelper {


    public class DBConstants {

        public static final String TABLE_NAME = "Contents";
        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CHECKSUM = "checksum";
        public static final String COLUMN_SYNCED = "synced";
        public static final String COLUMN_IS_SYNCING = "is_syncing";
        public static final String COLUMN_IS_RETURNED_ERROR = "is_returned_error";
        public static final String COLUMN_IS_DIRTY = "is_dirty";
        public static final String COLUMN_DATE_CREATED = "date_created";
        public static final String COLUMN_DATE_MODIFIED = "date_modified";
    }

    private SQLiteDatabase mDB;


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KEEP_IT_SAFE_DB";


    public ContentDAL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mDB = null;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + DBConstants.TABLE_NAME + "(" +
                        DBConstants.COLUMN_ID + " INTEGER," +
                        DBConstants.COLUMN_TYPE + " TEXT_TYPE," +
                        DBConstants.COLUMN_CHECKSUM + " TEXT_TYPE," +
                        DBConstants.COLUMN_SYNCED +" INTEGER,"+
                        DBConstants.COLUMN_IS_SYNCING +" INTEGER,"+
                        DBConstants.COLUMN_IS_RETURNED_ERROR +" INTEGER,"+
                        DBConstants.COLUMN_IS_DIRTY +" INTEGER,"+
                        DBConstants.COLUMN_DATE_CREATED +" LONG,"+
                        DBConstants.COLUMN_DATE_MODIFIED +" LONG)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME);
    }



    public void InsertContent(String type, int id, String checksum) {
        // When new content add to device

        ContentValues values;


        // Check if content already exist
        if (getContentIfExist(type, id) == null) {


            values = new ContentValues();

            values.put(DBConstants.COLUMN_ID, id);
            values.put(DBConstants.COLUMN_TYPE, type);
            values.put(DBConstants.COLUMN_CHECKSUM, checksum);
            values.put(DBConstants.COLUMN_SYNCED, 0);
            values.put(DBConstants.COLUMN_IS_SYNCING, 0);
            values.put(DBConstants.COLUMN_IS_RETURNED_ERROR, 0);
            values.put(DBConstants.COLUMN_IS_DIRTY, 0);
            values.put(DBConstants.COLUMN_DATE_CREATED, System.currentTimeMillis());
            values.put(DBConstants.COLUMN_DATE_MODIFIED, System.currentTimeMillis());

            mDB = getWritableDatabase();
            mDB.insertOrThrow(DBConstants.TABLE_NAME, null, values);

            mDB.close();
        }
    }

    public void DeleteContent(String type, int id) {

        String query;

        mDB  = this.getWritableDatabase();
        query = DBConstants.COLUMN_ID + " = ? AND " + DBConstants.COLUMN_TYPE + " = ?";
        mDB.delete(DBConstants.TABLE_NAME, query,new String[]{ String.valueOf(id), type } );
        mDB.close();
    }

    public void CancelAllInSync(String type) {
        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_IS_SYNCING + " = 0 " +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type + "'");

        mDB.close();
    }

    // Setters
    public void setInSync(int id, boolean flag, String type) {


        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_IS_SYNCING + " = " + value +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'");

        mDB.close();
    }

    public void setChecksum(int id, String checksum, String type) {



        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_CHECKSUM + " = " + checksum +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'");

        mDB.close();
    }

    public void setReturnedError(int id, boolean flag, String type) {
        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_IS_RETURNED_ERROR + " = " + value +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'");

        mDB.close();
    }

    public void setSynced(int id, boolean flag, String type) {
        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_SYNCED + " = " + value +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'");

        mDB.close();
    }

    public void setDirty(int id, boolean flag, String type) {
        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_IS_DIRTY + " = " + value +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'");

        mDB.close();
    }

    public void setDateModified(int id, long timeStamp, String type) {


        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET " +
                DBConstants.COLUMN_DATE_MODIFIED + " = " + timeStamp +
                " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'");

        mDB.close();
    }

    // ID Getters
    public DBContent getNextToSync(String type) {

        DBContent result = null;

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT * " +
                        " FROM "+ DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                        "' AND " + DBConstants.COLUMN_SYNCED + " = '0'" +
                        " AND " + DBConstants.COLUMN_IS_RETURNED_ERROR + " = '0'" +
                        " AND " + DBConstants.COLUMN_IS_SYNCING + " = '0' LIMIT 1"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            if (cursor.getCount() > 0 && cursor.moveToNext()) {

                result = new DBContent(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        ((cursor.getInt(3) == 1)),
                        ((cursor.getInt(4) == 1)),
                        ((cursor.getInt(5) == 1)),
                        ((cursor.getInt(6) == 1)),
                        cursor.getLong(7),
                        cursor.getLong(8));
            }
            cursor.close();
        }

        mDB.close();

        return result;
    }

    public DBContent getById(int id,String type) {

        DBContent result = null;

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT * " +
                        " FROM "+ DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                        "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            if (cursor.getCount() > 0 && cursor.moveToNext()) {

                result = new DBContent(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        ((cursor.getInt(3) == 1)),
                        ((cursor.getInt(4) == 1)),
                        ((cursor.getInt(5) == 1)),
                        ((cursor.getInt(6) == 1)),
                        cursor.getLong(7),
                        cursor.getLong(8));
            }
            cursor.close();
        }

        mDB.close();

        return result;
    }

    public int getLastIDByContentType(String type) {

        int result = 0;

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT max(" + DBConstants.COLUMN_ID + ")" +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type + "'"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = cursor.getInt(0);
            }
            cursor.close();
        }

        mDB.close();

        return result;
    }

    public DBContent getContentIfExist(String type, int id) {

        // If exists returns the content else return null

        DBContent result = null;

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT " + DBConstants.COLUMN_ID + "," + DBConstants.COLUMN_TYPE + "," + DBConstants.COLUMN_CHECKSUM +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type +
                        "' AND " + DBConstants.COLUMN_ID + " = '" + id + "'"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                result = new DBContent(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
            }
            cursor.close();
        }

        mDB.close();


        return result;
    }


    public boolean getDirty(int id, String type) {

        Cursor cursor;
        boolean result = false;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT " + DBConstants.COLUMN_IS_DIRTY +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type + "'" +
                        " AND " + DBConstants.COLUMN_ID + " = '" + id + "'"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            if (cursor.moveToNext()) {

                result = cursor.getInt(0) == 1;

            }

            cursor.close();
        }

        mDB.close();

        return result;

    }


    public List<DBContent> getAllContents(String type) {

        List<DBContent> result;

        result = new ArrayList<DBContent>();

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT * " +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_TYPE + " = '" + type + "' ORDER BY Id DESC"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            while (cursor.getCount() > 0 && cursor.moveToNext()) {

                DBContent dbContent;
                dbContent = new DBContent(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        ((cursor.getInt(3) == 1)),
                        ((cursor.getInt(4) == 1)),
                        ((cursor.getInt(5) == 1)),
                        ((cursor.getInt(6) == 1)),
                        cursor.getLong(7),
                        cursor.getLong(8));

                result.add(dbContent);
            }
            cursor.close();
        }

        mDB.close();


        return result;
    }

    public List<DBContent> getAllUnsyncedContents(String type) {

        List<DBContent> result;

        result = new ArrayList<DBContent>();

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT * " +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_SYNCED + " = '0'" +
                        " AND " + DBConstants.COLUMN_IS_SYNCING + " = '0'" +
                        " AND " + DBConstants.COLUMN_IS_RETURNED_ERROR + " = '0'" +
                        ( (type != null) ? " AND " + DBConstants.COLUMN_TYPE + " = '" + type + "'" : "")
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            while (cursor.getCount() > 0 && cursor.moveToNext()) {

                DBContent dbContent;
                dbContent = new DBContent(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        ((cursor.getInt(3) == 1)),
                        ((cursor.getInt(4) == 1)),
                        ((cursor.getInt(5) == 1)),
                        ((cursor.getInt(6) == 1)),
                        cursor.getLong(7),
                        cursor.getLong(8));

                result.add(dbContent);

            }
            cursor.close();
        }

        mDB.close();


        return result;
    }


/*
    public List<DBContent> getAllSyncedContents(String type) {
        List<Integer> result;

        result = new ArrayList<Integer>();

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT " + DBConstants.COLUMN_ID +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_SYNCED + " = 1" +
                        " AND " + DBConstants.COLUMN_TYPE + " = " + type + ")"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            while (cursor.getCount() > 0 && cursor.moveToNext()) {
                result.add(cursor.getInt(0));
            }
            cursor.close();
        }

        mDB.close();


        return result;
    }



    public List<DBContent> getAllInSyncContents(String type) {
        List<Integer> result;

        result = new ArrayList<Integer>();

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT " + DBConstants.COLUMN_ID +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_IS_SYNCING + " = 1" +
                        " AND " + DBConstants.COLUMN_TYPE + " = " + type + ")"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            while (cursor.getCount() > 0 && cursor.moveToNext()) {
                result.add(cursor.getInt(0));
            }
            cursor.close();
        }

        mDB.close();


        return result;
    }

    public List<DBContent> getAllErrorContents(String type) {
        List<Integer> result;

        result = new ArrayList<Integer>();

        Cursor cursor;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT " + DBConstants.COLUMN_ID +
                        " FROM " + DBConstants.TABLE_NAME +
                        " WHERE " + DBConstants.COLUMN_IS_RETURNED_ERROR + " = 1" +
                        " AND " + DBConstants.COLUMN_TYPE + " = " + type + ")"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            while (cursor.getCount() > 0 && cursor.moveToNext()) {
                result.add(cursor.getInt(0));
            }
            cursor.close();
        }

        mDB.close();


        return result;
    }*/

}
