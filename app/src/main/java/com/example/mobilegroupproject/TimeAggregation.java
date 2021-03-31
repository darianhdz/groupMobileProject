package com.example.mobilegroupproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class TimeAggregation extends AppCompatActivity{

    Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);
        createSpinner();
    }

    public void dataClick(View view)
    {
        MyDialogFragment f = new MyDialogFragment();
        f.show(getSupportFragmentManager(), "data");
    }

    public void goBack(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    public void createSpinner()
    {
        mCursor = getContentResolver().query(contentProvider.CONTENT_URI, null, null, null, null);
        int i = 0;
        Spinner selection = (Spinner) findViewById(R.id.dataSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        while (i < mCursor.getCount()) {
            if (mCursor.getCount() > 0) {
                mCursor.moveToNext();
                adapter.add(mCursor.getString(1));
                Log.d("here1", mCursor.getString(1));
                Log.d("here", String.valueOf(mCursor.getInt(2)));
                i++;
            }
        }
        selection.setAdapter(adapter);
    }
}
