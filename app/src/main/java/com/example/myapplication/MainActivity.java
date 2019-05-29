package com.example.myapplication;



import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TSTask.JsonListener jsonListener;
    private Context context = this;
    private GraphView graphView1;
    private GraphView graphView2;

    private DataPoint dataPoint1[] = {
            new DataPoint(0, 1),
            new DataPoint(1, 2),
            new DataPoint(2, 3),
            new DataPoint(3, 4),
            new DataPoint(4, 5)
    };

    private DataPoint dataPoint2[] = {
            new DataPoint(0, 1),
            new DataPoint(1, 2),
            new DataPoint(2, 3),
            new DataPoint(3, 4),
            new DataPoint(4, 5)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.icon1);
        Drawable d=getResources().getDrawable(R.drawable.barbackground);
        actionbar.setBackgroundDrawable(d);


        graphView1 = findViewById(R.id.graph1);
        graphView2 = findViewById(R.id.graph2);
        graphView1.getViewport().setXAxisBoundsManual(true);
        graphView1.getViewport().setMinX(0);
        graphView1.getViewport().setMaxX(4);
        graphView1.getViewport().setYAxisBoundsManual(true);
        graphView1.getViewport().setMinY(20.0);
        graphView1.getViewport().setMaxY(40.0);
        graphView2.getViewport().setXAxisBoundsManual(true);
        graphView2.getViewport().setMinX(0);
        graphView2.getViewport().setMaxX(4);
        graphView2.getViewport().setYAxisBoundsManual(true);
        graphView2.getViewport().setMinY(0.0);
        graphView2.getViewport().setMaxY(80.0);
        graphView1.setBackgroundColor(Color.argb(50, 50, 0, 200));
        graphView1.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLUE);
        graphView1.getGridLabelRenderer().setVerticalLabelsColor(Color.BLUE);
        graphView2.setBackgroundColor(Color.argb(50, 50, 0, 200));
        graphView2.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLUE);
        graphView2.getGridLabelRenderer().setVerticalLabelsColor(Color.BLUE);

        jsonListener = new TSTask.JsonListener() {
            @Override
            public void onStreamResponse(List<Model> feeds) {

                // Display data temperature
                graphView1.removeAllSeries();
                dataPoint1[0] = new DataPoint(0, dataPoint1[1].getY());
                dataPoint1[1] = new DataPoint(1, dataPoint1[2].getY());
                dataPoint1[2] = new DataPoint(2, dataPoint1[3].getY());
                dataPoint1[3] = new DataPoint(3, dataPoint1[4].getY());
                dataPoint1[4] = new DataPoint(4, feeds.get(0).field1);

                LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(dataPoint1);
                graphView1.addSeries(series1);
                series1.setColor(Color.RED);
                series1.setDrawDataPoints(true);
                series1.setDataPointsRadius(10);
                series1.setThickness(8);
                // Display data light level
                graphView2.removeAllSeries();
                dataPoint2[0] = new DataPoint(0, dataPoint2[1].getY());
                dataPoint2[1] = new DataPoint(1, dataPoint2[2].getY());
                dataPoint2[2] = new DataPoint(2, dataPoint2[3].getY());
                dataPoint2[3] = new DataPoint(3, dataPoint2[4].getY());
                dataPoint2[4] = new DataPoint(4, feeds.get(0).field2);

                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dataPoint2);
                graphView2.addSeries(series2);
                series2.setColor(Color.YELLOW);
                series2.setDrawDataPoints(true);
                series2.setDataPointsRadius(10);
                series2.setThickness(8);


            }

            @Override
            public void onStreamError(List<Model> feeds) {
            }
        };

        runTSTask();
    }

    public void runTSTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doTSTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new TSTask(context,jsonListener).execute("https://api.thingspeak.com/channels/783164/feeds.json?api_key=9RZZI3V1IKQC74FX&results=1");
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doTSTask, 0, 5000);
    }
}


