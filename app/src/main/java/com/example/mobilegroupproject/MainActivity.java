package com.example.mobilegroupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    Cursor mCursor;
    static Chronometer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = (Chronometer) findViewById(R.id.timer);
        Button stop = (Button) findViewById(R.id.stopTimer);
        stop.setVisibility(View.INVISIBLE);
        stop.setClickable(false);
        createSpinner();
    }

    public void startTimer(View view)
    {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        Button stop = (Button) findViewById(R.id.stopTimer);
        stop.setVisibility(View.VISIBLE);
        stop.setClickable(true);
        Button start = (Button) findViewById(R.id.startTimer);
        start.setVisibility(View.INVISIBLE);
        start.setClickable(false);
    }

    public void stopTimer(View view)
    {
        long timePassed = SystemClock.elapsedRealtime() - timer.getBase();
        timePassed /= 1000;
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        Button start = (Button) findViewById(R.id.startTimer);
        start.setVisibility(View.VISIBLE);
        start.setClickable(true);
        Button stop = (Button) findViewById(R.id.stopTimer);
        stop.setVisibility(View.INVISIBLE);
        stop.setClickable(false);

        ContentValues mUpdateValues = new ContentValues();
        Spinner spinner = (Spinner) findViewById(R.id.profileSelection);

        String mSelectionClause = contentProvider.COLUMN_PROFILES + " = ? ";
        String[] mSelectionArgs = { spinner.getSelectedItem().toString()};
        long savedTime = 0;
        mCursor = getContentResolver().query(contentProvider.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);
        if (mCursor.getCount() > 0) {
            mCursor.moveToNext();
            savedTime = mCursor.getInt(2);
        }
        mUpdateValues.put(contentProvider.COLUMN_PROFILES, spinner.getSelectedItem().toString());
        mUpdateValues.put(contentProvider.COLUMN_TIME, timePassed + savedTime);
        int mRowsUpdated = 0;
        mRowsUpdated = getContentResolver().update(contentProvider.CONTENT_URI, mUpdateValues,
                mSelectionClause, mSelectionArgs);
    }

    public void addEntries(View view)
    {
        MyDialogFragment f = new MyDialogFragment();
        f.show(getSupportFragmentManager(), "add");
    }

    public void removeEntries(View view)
    {
        MyDialogFragment f = new MyDialogFragment();
        f.show(getSupportFragmentManager(), "remove");
    }

    public void createSpinner()
    {
        mCursor = getContentResolver().query(contentProvider.CONTENT_URI, null, null, null, null);
        int i = 0;
        Spinner selection = (Spinner) findViewById(R.id.profileSelection);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        while (i < mCursor.getCount()) {
            if (mCursor.getCount() > 0) {
                mCursor.moveToNext();
                adapter.add(mCursor.getString(1));
                Log.d("here", mCursor.getString(1));
                Log.d("here", String.valueOf(mCursor.getInt(2)));
                i++;
            }
        }
        selection.setAdapter(adapter);
    }
}