package com.example.mobilegroupproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

public class TimeAggregation extends AppCompatActivity{
    GraphView graph;
    Cursor mCursor;
    TextView MyTime;
    TextView AvgTimeStr;
    TextView MyTotalTime;
    TextView MyTotalSesTime;
    TextView TotalTimeStr;
    TextView SessionTot;
    TextView SessionTotalStr;
    TextView DayLong;
    TextView FirstDay;
    TextView TotDay;

    Spinner selection;

    ScrollView myScroll;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);
        myScroll = (ScrollView)findViewById(R.id.MyScrollView);
        createSpinner();
    }
    public void makeGraph()
    {
        GraphView g = (GraphView) findViewById(R.id.SessionGraph);  //Hours per Session
        //GraphView h = (GraphView) findViewById(R.id.SessionGraph);  //Hours per Day
        MyTime = (TextView)findViewById(R.id.Text_TimeSpent);
        MyTotalTime= (TextView)findViewById(R.id.Text_TimeTotal);
        SessionTot= (TextView)findViewById(R.id.Text_SessionTotal);
        DayLong = (TextView)findViewById(R.id.Text_LongDay);
        FirstDay = (TextView)findViewById(R.id.Text_FirstDay);
        TotDay = (TextView)findViewById(R.id.Text_TotalDays);
        MyTotalSesTime = (TextView)findViewById(R.id.Text_TimeSpentSes);


        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> seriesDate = new LineGraphSeries<>();

        double TotalTime = 0;
        long first_date = 0;
        int longestses = 0;
        int longestsesLen = 0;
        long longestsesDate= 0;
        long LastDay = 0;

        int totalDays = 0;
        int totalSess = 0;


        Vector<Double> SessionV = new Vector<Double>();
        Vector<Long> DayV = new Vector<Long>();


        String mSelectionClause = contentProvider.COLUMN_PROFILES + " = ? ";
        String[] mSelectionArgs = { selection.getSelectedItem().toString()};
        mCursor = getContentResolver().query(contentProvider.CONTENT_URIOne, null, mSelectionClause, mSelectionArgs, null);
        int i = 0;
        while (i < mCursor.getCount()) {
            if (mCursor.getCount() > 0) {
                mCursor.moveToNext();
                SessionV.add(mCursor.getDouble(2) / 60 );
                TotalTime += mCursor.getDouble(2) / 60;
                DataPoint myDP = new DataPoint(i, mCursor.getDouble(2));
                series.appendData(myDP,true,100);
                if (longestsesLen < mCursor.getInt(2)) {
                    longestses = i;
                    longestsesDate= mCursor.getLong(3);
                    longestsesLen = mCursor.getInt(2) / 60;
                }
                if ((first_date > mCursor.getLong(3)) || (first_date == 0))
                {
                    first_date = mCursor.getLong(3);
                }
                if (LastDay < mCursor.getLong(3))
                {
                    LastDay = mCursor.getLong(3);
                }

                seriesDate.appendData(new DataPoint(i, (mCursor.getLong(3) - first_date)),false, 100);
                totalSess = i++;
            }
        }

        totalDays = (int)(Math.ceil(LastDay - first_date)/86400000);

        /*
        for (int x = 0; x < y2.length; x++)
        {
            DataPoint myDP = new DataPoint(x, y2[x]);
            sumofY2 += y2[x];
            series2.appendData(myDP,true, 100);
            series2.setOnDataPointTapListener(new OnDataPointTapListener()
            {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getApplicationContext(), "Day " + (int)dataPoint.getX() + "\nHours " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "On day " + (int)dataPoint.getX() + " you spent "+dataPoint.getY() + " hours.", Toast.LENGTH_SHORT).show();
                }

            });
        }
        */

        g.addSeries(series);
        //g.addSeries(series2);

        //series2.setColor(Color.RED);
        series.setColor(Color.BLUE);
        GridLabelRenderer gridLabel = g.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Sessions");
        gridLabel.setVerticalAxisTitle("Time");
        if (totalSess == 0)
        {
            totalSess ++;   //This is in case someone decides to be funny
                            //And try to enter the Time Aggregation
                            //Without starting a single session
        }
        if (totalDays == 0)
        {
            totalDays++;
        }

        MyTotalTime.setText(df2.format(TotalTime) + " Minutes. ");
        MyTime.setText((df2.format((TotalTime)/totalSess)) + " minutes per session.");
        MyTotalSesTime.setText((df2.format((TotalTime)/totalDays)) + " Hours per day.");
        SessionTot.setText(totalSess + " Sessions");
        DayLong.setText( new Date(longestsesDate).toString() + " (" + longestsesLen + " minutes)");
        FirstDay.setText(new Date(first_date).toString());
        TotDay.setText(totalDays + " Days");

       myScroll.setVisibility(View.VISIBLE);

        g.getViewport().setScalable(true);
        g.getViewport().setScrollable(true);


    }

    public void dataClick(View view)
    {
        MyDialogFragment f = new MyDialogFragment();
        makeGraph();
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
        selection = (Spinner) findViewById(R.id.dataSpinner);
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
