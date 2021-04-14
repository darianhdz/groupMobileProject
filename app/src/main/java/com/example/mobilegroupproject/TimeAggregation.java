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
        //AvgTimeStr = (TextView)findViewById(R.id.Text_TimeSpentString);
        MyTotalTime= (TextView)findViewById(R.id.Text_TimeTotal);
        SessionTot= (TextView)findViewById(R.id.Text_SessionTotal);
        DayLong = (TextView)findViewById(R.id.Text_LongDay);
        FirstDay = (TextView)findViewById(R.id.Text_FirstDay);
        TotDay = (TextView)findViewById(R.id.Text_TotalDays);
        MyTotalSesTime = (TextView)findViewById(R.id.Text_TimeSpentSes);
        //TotalTimeStr=(TextView)findViewById(R.id.Text_TotalTimeString);


        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> seriesAvg = new LineGraphSeries<>();

        LineGraphSeries<DataPoint> series2= new LineGraphSeries<>();    //Days
        double sumofY = 0;
        double sumofY2= 0;
        double longofY2 = 0;

        double[] y = {1,2.2, 1.7, 8.1,0.3,0.1,0.2,3.2, 2.7, 6.1, 1.2,1.5, 8.1,0.3,0.6,0.3,3.2, 2.7, 6.1}; //Demo of hours spent on an assignment
        double[] y2= {12.8, 5.9,8.8,8.4,0,0,4.4, 3.6, 2.2,1.3, 6.3, 4.1};
        String[] w = {"Writing Code", "Debugging", "Testing", "Writing Code", "Break"};
        for (int x = 0; x < y.length; x++)
        {
            DataPoint myDP = new DataPoint(x, y[x]);
            DataPoint AvDP = new DataPoint(x, sumofY/x);
            sumofY += y[x];
            series.appendData(myDP,true, 100);
            seriesAvg.appendData(AvDP, true, 100);
            series.setOnDataPointTapListener(new OnDataPointTapListener()
            {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getApplicationContext(), "Session " + (int)dataPoint.getX() + "\nDescription: " + w[(int)dataPoint.getY() % w.length] + "\nHours " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "On day " + (int)dataPoint.getX() + " you spent "+dataPoint.getY() + " hours.", Toast.LENGTH_SHORT).show();
                }

            });
        }
        for (int x = 0; x < y2.length; x++)
        {
            if (longofY2 < y2[x])
            {
                longofY2 = y2[x];
            }
            sumofY2 += y2[x];
        }

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
        g.addSeries(seriesAvg);
        //g.addSeries(series2);

        //series2.setColor(Color.RED);
        series.setColor(Color.BLUE);
        seriesAvg.setColor(Color.RED);
        GridLabelRenderer gridLabel = g.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Sessions");
        gridLabel.setVerticalAxisTitle("Time");

        MyTime.setText((df2.format(sumofY / y.length)) + " Hours per session.");
        MyTotalSesTime.setText((df2.format(sumofY2 / y2.length)) + " Hours per day.");
        MyTotalTime.setText(df2.format(sumofY) + " Hours. ");
        SessionTot.setText(y.length + " Sessions");
        DayLong.setText("March 29 2021 (" + longofY2 + " hours)");
        FirstDay.setText("March 19 2021");
        TotDay.setText(y2.length + " Days");

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
