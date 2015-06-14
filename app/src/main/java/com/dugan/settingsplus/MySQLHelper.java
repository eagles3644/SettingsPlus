package com.dugan.settingsplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Todd on 12/7/2014.
 */
public class MySQLHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SettingsPlus.db";

    //Log Table Vars
    public static final String LOG_TABLE_NAME = "LOG";
    public static final String LOG_ID = "_id";
    public static final String LOG_ACTION = "action";
    public static final String LOG_DATETIME = "datetime";

    //Trigger Table Vars
    public static final String TRIGGER_TABLE_NAME = "TRIGGER";
    public static final String TRIGGER_ID = "_id";
    public static final String TRIGGER_PROF_ID = "prof_id";
    public static final String TRIGGER_TYPE = "type";
    public static final String TRIGGER_NAME = "name";
    public static final String TRIGGER_TIME = "time";
    public static final String TRIGGER_SUN = "sunday";
    public static final String TRIGGER_MON = "monday";
    public static final String TRIGGER_TUES = "tuesday";
    public static final String TRIGGER_WED = "wednesday";
    public static final String TRIGGER_THURS = "thursday";
    public static final String TRIGGER_FRI = "friday";
    public static final String TRIGGER_SAT = "saturday";
    public static final String TRIGGER_BAT_LEVEL = "bat_level";
    public static final String TRIGGER_LOCATION = "location";
    public static final String TRIGGER_DELETE_IND = "deleted";
    public static final String TRIGGER_EXPAND_IND = "expanded";

    //Profile Table Vars
    public static final String PROFILE_TABLE_NAME = "PROFILE";
    public static final String PROFILE_ID = "_id";
    public static final String PROFILE_NAME = "name";
    public static final String PROFILE_AUTO_BRIGHT = "auto_bright";
    public static final String PROFILE_MANUAL_BRIGHT = "manual_bright";
    public static final String PROFILE_RINGTONE = "ringtone";
    public static final String PROFILE_NOTIFICATION_TONE = "notification_tone";
    public static final String PROFILE_VIBRATE = "vibrate";
    public static final String PROFILE_BLUETOOTH = "bluetooth";
    public static final String PROFILE_DATA = "data";
    public static final String PROFILE_WIFI = "wifi";
    public static final String PROFILE_DELETE_IND = "deleted";
    public static final String PROFILE_EXPAND_IND = "expanded";

    //Create Statements
    private static final String SQL_CREATE_LOG_TBL = "CREATE TABLE " + LOG_TABLE_NAME + " (" +
            LOG_ID + " INTEGER PRIMARY KEY, " +
            LOG_ACTION + " TEXT, " + LOG_DATETIME + " TEXT)";

    private static final String SQL_CREATE_TRIGGER_TBL = "CREATE TABLE " + TRIGGER_TABLE_NAME + " (" +
            TRIGGER_ID + " INTEGER PRIMARY KEY, " + TRIGGER_PROF_ID + " INTEGER, " +
            TRIGGER_TYPE + " TEXT, " + TRIGGER_NAME + " TEXT, " + TRIGGER_TIME + " TEXT, " +
            TRIGGER_SUN + " TEXT, " + TRIGGER_MON + " TEXT, " + TRIGGER_TUES + " TEXT, " +
            TRIGGER_WED + " TEXT, " + TRIGGER_THURS + " TEXT, " + TRIGGER_FRI +
            " TEXT, " + TRIGGER_SAT + " TEXT, " + TRIGGER_BAT_LEVEL + " INTEGER, " +
            TRIGGER_LOCATION + " TEXT, " + TRIGGER_DELETE_IND + " TEXT, " +
            TRIGGER_EXPAND_IND + " TEXT)";

    private static final String SQL_CREATE_PROFILE_TBL = "CREATE TABLE " + PROFILE_TABLE_NAME + " (" +
            PROFILE_ID + " INTEGER PRIMARY KEY, " + PROFILE_NAME + " TEXT, " +
            PROFILE_AUTO_BRIGHT + " TEXT, " + PROFILE_MANUAL_BRIGHT + " INTEGER, " +
            PROFILE_RINGTONE + " TEXT, " + PROFILE_NOTIFICATION_TONE + " TEXT, " +
            PROFILE_VIBRATE + " TEXT, " + PROFILE_BLUETOOTH + " TEXT, " +
            PROFILE_DATA + " TEXT, " + PROFILE_WIFI + " TEXT, " + PROFILE_DELETE_IND + " TEXT, " +
            PROFILE_EXPAND_IND + " TEXT)";

    public MySQLHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOG_TBL);
        db.execSQL(SQL_CREATE_TRIGGER_TBL);
        db.execSQL(SQL_CREATE_PROFILE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean profileExists(String profName){
        Boolean exists = false;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + PROFILE_ID + " FROM " + PROFILE_TABLE_NAME + " WHERE " + PROFILE_NAME + " = '" + profName + "'";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        if (count > 0){
            exists = true;
        }
        return exists;
    }

    public void addProfile(String profName){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, profName);
        values.put(PROFILE_AUTO_BRIGHT, "Y");
        values.put(PROFILE_MANUAL_BRIGHT, 100);
        values.put(PROFILE_RINGTONE, "DEFAULT");
        values.put(PROFILE_NOTIFICATION_TONE, "DEFAULT");
        values.put(PROFILE_VIBRATE, "Y");
        values.put(PROFILE_BLUETOOTH, "Y");
        values.put(PROFILE_DATA, "Y");
        values.put(PROFILE_WIFI, "Y");
        values.put(PROFILE_DELETE_IND, "N");
        values.put(PROFILE_EXPAND_IND, "N");
        db.insert(PROFILE_TABLE_NAME, null, values);
    }

    public Cursor getProfiles(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + PROFILE_TABLE_NAME + " WHERE " + PROFILE_DELETE_IND + "='N'";
        return db.rawQuery(query, null);
    }

    public void upProfExpandInd(String value, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_EXPAND_IND, value);
        db.update(PROFILE_TABLE_NAME, values, PROFILE_ID + "=" + id, null);
    }

    public void upProfName(String value, int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, value);
        db.update(PROFILE_TABLE_NAME, values, PROFILE_ID + "=" + id, null);
    }

    public void upProfAutoBright(boolean bolValue, int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        String value = "Y";
        if(!bolValue){
            value = "N";
        }
        values.put(PROFILE_AUTO_BRIGHT, value);
        db.update(PROFILE_TABLE_NAME, values, PROFILE_ID + "=" + id, null);
    }

    public void upProfManBright(int value, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_MANUAL_BRIGHT, value);
        db.update(PROFILE_TABLE_NAME, values, PROFILE_ID + "=" + id, null);
    }

    public void upProfRingtone(String value, int id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_RINGTONE, value);
        db.update(PROFILE_TABLE_NAME, values, PROFILE_ID + "=" + id, null);
    }

    public void upProfDeleted(String value, int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROFILE_DELETE_IND, value);
        db.update(PROFILE_TABLE_NAME, values, PROFILE_ID + "=" + id, null);
    }
}
