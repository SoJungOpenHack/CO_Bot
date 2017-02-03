package com.example.bluetoothapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    CobotDBOpenHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        mHelper = new CobotDBOpenHelper(getApplicationContext());
        mHelper.insertSample();

        BarChart barChart = (BarChart)findViewById(R.id.chartBar);
        List<BarEntry> entries = new ArrayList<BarEntry>();

        int xpos = 10;
        for ( int coData : mHelper.getDailyAverage() ) {
            Log.i("COData", ""+coData);
            entries.add(new BarEntry(xpos, coData));
            xpos += 20;
        }
        BarDataSet dataSet = new BarDataSet(entries, "평균 CO량");
        //dataSet.setFormSize(500.f);

        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.invalidate();

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0.f);
        leftAxis.setAxisMaximum(2500f);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setSpaceMin(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.f);
        xAxis.setAxisMaximum(100.f);
        xAxis.setDrawGridLines(false);




    }
}
