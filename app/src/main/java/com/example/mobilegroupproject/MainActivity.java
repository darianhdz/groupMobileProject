package com.example.mobilegroupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

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
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        Button start = (Button) findViewById(R.id.startTimer);
        start.setVisibility(View.VISIBLE);
        start.setClickable(true);
        Button stop = (Button) findViewById(R.id.stopTimer);
        stop.setVisibility(View.INVISIBLE);
        stop.setClickable(false);
    }

    public void addEntries(View view)
    {
        MyDialogFragment f = new MyDialogFragment();
        f.show(getSupportFragmentManager(), "dialog");
    }

    public void createSpinner()
    {
        mCursor = getContentResolver().query(contentProvider.CONTENT_URI, null, null, null, null);
        while (mCursor != null) {
            if (mCursor.getCount() > 0) {
                mCursor.moveToNext();
                Log.d("here", mCursor.getString(0));
            }
        }
    }
}