package com.example.mobilegroupproject;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class contentProvider extends ContentProvider {
    public final static String DBNAME = "AppDatabase";
    public final static String TABLE_PROFILESTABLE = "profilestable";
    public final static String COLUMN_PROFILES = "profilename";
    public final static String COLUMN_TIME = "profiletime";

    public static final String AUTHORITY = "com.example.mobilegroupproject";
    public static final Uri CONTENT_URI = Uri.parse(
            "content://com.example.mobilegroupproject/" + TABLE_PROFILESTABLE);

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

        long id = mOpenHelper.getWritableDatabase().insert(TABLE_PROFILESTABLE, null, values);

        return Uri.withAppendedPath(CONTENT_URI, "" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return mOpenHelper.getWritableDatabase().update(TABLE_PROFILESTABLE, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mOpenHelper.getWritableDatabase().delete(TABLE_PROFILESTABLE, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(TABLE_PROFILESTABLE, projection, selection, selectionArgs,
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
            db.execSQL(SQL_CREATE_MAIN);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        }
    }
}
