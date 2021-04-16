package com.example.mobilegroupproject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class contentProvider extends ContentProvider {
    public final static String DBNAME = "AppDatabase";
    public final static String TABLE_PROFILESTABLE = "profilestable";
    public final static String TABLE_SESSIONSTABLE = "sessionstable";
    public final static String TABLE_PROFILESESSIONSTABLE = "profilesessionstable";

    public final static String COLUMN_PROFILES = "profilename";
    public final static String COLUMN_TIME = "profiletime";

    public final static String COLUMN_PROID = "pid";
    public final static String COLUMN_SESID = "sid";
    public final static String COLUMN_date = "sessiondate";
    public final static String COLUMN_TEXT = "note";

    public final static String COLUMN_SESSIONNUM = "totalsessions";
    public final static String COLUMN_LONG = "longest";

    public final static String COLUMN_STIME = "time";

    public static final String AUTHORITY = "com.example.mobilegroupproject";
    public static final Uri CONTENT_URI = Uri.parse(
                "content://com.example.mobilegroupproject/" + TABLE_PROFILESTABLE);
    public static final Uri CONTENT_URIOne = Uri.parse(
            "content://com.example.mobilegroupproject/" + TABLE_SESSIONSTABLE);

    private static UriMatcher sUriMatcher;

    private MainDatabaseHelper mOpenHelper;

    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            TABLE_PROFILESTABLE +  // Table's name
            "(" +               // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            COLUMN_PROFILES +
            " TEXT," +
            COLUMN_TIME +
            " TEXT)";
    private static final String SQL_CREATE_MAINTWO = "CREATE TABLE " +
            TABLE_SESSIONSTABLE +  // Table's name
            "(" +               // The columns in the table
            " _SID INTEGER PRIMARY KEY, " +
            COLUMN_PROFILES +
            " TEXT," +
            COLUMN_TIME +
            " INTEGER," +
            COLUMN_date +
            " INTEGER," +
            COLUMN_TEXT +
            " TEXT)";

    private static final String SQL_CREATE_MAINONE = "CREATE TABLE " +
            TABLE_PROFILESTABLE +  // Table's name
            "(" +               // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            COLUMN_PROFILES +
            " TEXT," +
            COLUMN_STIME +
            " TEXT," +
            COLUMN_SESSIONNUM +
            " INTEGER," +
            COLUMN_LONG +
            " INTEGER)"
            ;
    @Override
    public boolean onCreate() {
        mOpenHelper = new MainDatabaseHelper(getContext());

        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String profile = values.getAsString(COLUMN_PROFILES).trim();

        if (profile.equals(""))
            return null;

        long id = mOpenHelper.getWritableDatabase().insert(uri.toString().substring(41), null, values);

        return Uri.withAppendedPath(CONTENT_URI, "" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.i("uriToString", uri.toString().substring(41));
        //mOpenHelper.getWritableDatabase().update(TABLE_SESSIONSTABLE, values, selection, selectionArgs);
        return mOpenHelper.getWritableDatabase().update(uri.toString().substring(41), values, selection, selectionArgs);

        //return mOpenHelper.getWritableDatabase().update(TABLE_PROFILESTABLE, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mOpenHelper.getWritableDatabase().delete(TABLE_PROFILESTABLE, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(uri.toString().substring(41), projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    protected static final class MainDatabaseHelper extends SQLiteOpenHelper {
        MainDatabaseHelper(Context context) {
            super(context, DBNAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("contentProvider", "onCreate: ");
            db.execSQL(SQL_CREATE_MAIN);
            Log.i("Database","DataBASED complete");
            db.execSQL(SQL_CREATE_MAINTWO);
            //db.execSQL(SQL_CREATE_MAINTWO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        }
    }
}
