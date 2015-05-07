package com.example.orensharon.finalproject.service.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by orensharon on 5/7/15.
 */
public class ContentDAL extends SQLiteOpenHelper {

    public class DBConstants {

        public static final String TABLE_NAME = "Contents";

        public static final String COLUMN_ID = "Id";
        public static final String COLUMN_SYNCED = "synced";
        public static final String COLUMN_IS_SYNCING = "is_syncing";
        public static final String COLUMN_IS_RETURED_ERROR = "is_returned_error";
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
                        DBConstants.COLUMN_ID + " INTEGER PRIMARY KEY," +
                        DBConstants.COLUMN_ID + " INTEGER," +
                        DBConstants.COLUMN_SYNCED +" BOOLEAN,"+
                        DBConstants.COLUMN_IS_SYNCING +" BOOLEAN,"+
                        DBConstants.COLUMN_IS_RETURED_ERROR +" BOOLEAN"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME);
    }

    public void InsertContent(String type, String id) {
        // When new content add to device

        ContentValues values;

        values = new ContentValues();

        values.put(DBConstants.COLUMN_ID, id);
        values.put(DBConstants.COLUMN_SYNCED, 0);
        values.put(DBConstants.COLUMN_IS_SYNCING, 0);
        values.put(DBConstants.COLUMN_IS_RETURED_ERROR, 0);


        mDB = getWritableDatabase();
        mDB.insertOrThrow(DBConstants.TABLE_NAME, null, values);

        mDB.close();
    }


    // Setters
    public void setInSync(String id, boolean flag) {


        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET "+
                DBConstants.COLUMN_IS_SYNCING + " = " + value +
                " WHERE " + DBConstants.COLUMN_ID + " = " + id);

        mDB.close();
    }

    public void setReturnedError(String id, boolean flag) {
        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET "+
                DBConstants.COLUMN_IS_RETURED_ERROR + " = " + value +
                " WHERE " + DBConstants.COLUMN_ID + " = " + id);

        mDB.close();
    }

    public void setSynced(String id, boolean flag) {
        int value;
        value = ((flag) ? 1 : 0);

        mDB  = this.getWritableDatabase();

        mDB.execSQL("UPDATE " + DBConstants.TABLE_NAME + " SET "+
                DBConstants.COLUMN_SYNCED + " = " + value +
                " WHERE " + DBConstants.COLUMN_ID + " = " + id);

        mDB.close();
    }

    // Getters
    public String getNextInSync() {

        String result = null;

        Cursor cursor;
        boolean bol;

        bol = false;

        mDB = getReadableDatabase();
        cursor = mDB.rawQuery(
                "SELECT " + DBConstants.COLUMN_ID +
                        " FROM "+ DBConstants.TABLE_NAME +
                        " WHERE Id = (SELECT min(Id) FROM " + DBConstants.TABLE_NAME + ")"
                ,null
        );

        if (cursor != null) {
            // Make sure the query is not empty result
            if (cursor.getCount() > 0 && cursor.moveToNext()) {
                  result = cursor.getString(0);
            }
            cursor.close();
        }

        mDB.close();

        return result;
    }

}
