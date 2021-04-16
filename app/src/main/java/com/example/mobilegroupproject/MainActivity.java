package com.example.mobilegroupproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "timer";
    Cursor mCursor;
    static Chronometer timer;
    String selection;
    static Button startButton;
    static Button stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        timer = (Chronometer) findViewById(R.id.timer);
        startButton = findViewById(R.id.startTimer);
        stop = (Button) findViewById(R.id.stopTimer);
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
        Spinner spinner = (Spinner) findViewById(R.id.profileSelection);
        ((Spinner) spinner).getSelectedView().setEnabled(false); //source : https://stackoverflow.com/questions/7641879/how-do-i-make-a-spinners-disabled-state-look-disabled
        spinner.setEnabled(false);
        selection = spinner.getSelectedItem().toString();
        Intent intent1 = new Intent(this, NotifService.class);
        intent1.putExtra("selection", selection);
        startService(intent1);
    }

    public void stopTimer(View view)
    {

        Spinner spinner = (Spinner) findViewById(R.id.profileSelection);
        ((Spinner) spinner).getSelectedView().setEnabled(true);
        spinner.setEnabled(true);
        long timePassed = SystemClock.elapsedRealtime() - timer.getBase();
        timePassed /= 1000;
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
        Intent intent1 = new Intent(this, NotifService.class);
        stopService(intent1);
        Button start = (Button) findViewById(R.id.startTimer);
        start.setVisibility(View.VISIBLE);
        start.setClickable(true);
        Button stop = (Button) findViewById(R.id.stopTimer);
        stop.setVisibility(View.INVISIBLE);
        stop.setClickable(false);

        ContentValues mUpdateValues = new ContentValues();

        spinner = (Spinner) findViewById(R.id.profileSelection);
        String mSelectionClause = contentProvider.COLUMN_PROFILES + " = ? ";
        String[] mSelectionArgs = { spinner.getSelectedItem().toString()};
        long savedTime = 0;
        mCursor = getContentResolver().query(contentProvider.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);
        if (mCursor.getCount() > 0) {
            mCursor.moveToNext();
            savedTime = mCursor.getInt(2);
        }
        ContentValues mInsertValues = new ContentValues();

        mUpdateValues.put(contentProvider.COLUMN_PROFILES, spinner.getSelectedItem().toString());
        mUpdateValues.put(contentProvider.COLUMN_TIME, timePassed + savedTime);

        mInsertValues.put(contentProvider.COLUMN_PROFILES, spinner.getSelectedItem().toString());
        mInsertValues.put(contentProvider.COLUMN_TIME, timePassed);
        mInsertValues.put(contentProvider.COLUMN_date, new Date().getTime());
        mInsertValues.put(contentProvider.COLUMN_TEXT, "tEST");


        int mRowsUpdated = 0;
        mRowsUpdated = getContentResolver().update(contentProvider.CONTENT_URI, mUpdateValues,
                mSelectionClause, mSelectionArgs);
        getContentResolver().insert(contentProvider.CONTENT_URIOne,mInsertValues);
        selection = null;
    }

    public void addEntries()
    {
        MyDialogFragment f = new MyDialogFragment();
        f.show(getSupportFragmentManager(), "add");
    }

    public void removeEntries()
    {
        MyDialogFragment f = new MyDialogFragment();
        f.show(getSupportFragmentManager(), "remove");
    }

    public void createSpinner()
    {
        mCursor = getContentResolver().query(contentProvider.CONTENT_URI, null, null, null, null);
        int i = 0;
        Spinner selection = (Spinner) findViewById(R.id.profileSelection);
        startButton.setClickable(mCursor.getCount() > 0);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        while (i < mCursor.getCount()) {
            if (mCursor.getCount() > 0) {
                mCursor.moveToNext();
                adapter.add(mCursor.getString(1));
                i++;
            }
        }
        selection.setAdapter(adapter);
    }

    public void toDataActivity()
    {
        Intent intent = new Intent(getApplicationContext(), TimeAggregation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //from contextmenu example
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addActivity:
                addEntries();
                break;
            case R.id.removeActivity:
                removeEntries();
                break;

            case R.id.showTimeData:
                toDataActivity();
                break;

            case R.id.changeBackground:
                Intent pickPic = new Intent(Intent.ACTION_PICK);
                pickPic.setType("image/*");
                startActivityForResult(pickPic, 1);
                break;

            case R.id.changeTimerColor:
                MyDialogFragment f = new MyDialogFragment();
                f.show(getSupportFragmentManager(), "color");

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri); //https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
                //for help on picking images from a gallery
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView background = findViewById(R.id.backgroundView);
                background.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "timerNotif";
            String description = "timer notification";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("method").equals("stop")){
            stopTimer(findViewById(R.id.stopTimer));
        }
    }

}