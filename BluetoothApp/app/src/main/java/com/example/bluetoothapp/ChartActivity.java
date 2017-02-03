package com.example.bluetoothapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {
    CobotDBOpenHelper mHelper;
    List<Integer> mListCO;
    List<Float> mListTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);


        mHelper = new CobotDBOpenHelper(getApplicationContext());

        mHelper.insertSample();

        mListCO = mHelper.getDailyAverage();
        mListTime = mHelper.getDailyTime();
        initDailyCOChart();

        Button btnSample = (Button)findViewById(R.id.btnSample);
        btnSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.insertSample();
                mListCO = mHelper.getDailyAverage();
                mListTime = mHelper.getDailyTime();

                initDailyCOChart();
            }
        });
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.clear();
                mListCO = mHelper.getDailyAverage();
                mListTime = mHelper.getDailyTime();

                //initDailyCOChart();
            }
        });
    }

    private void initDailyCOChart() {
        CombinedChart combinedChart = (CombinedChart) findViewById(R.id.chart);

        BarData barData = getBarDataCO();
        getLineDataTime();
        CombinedData finalData = new CombinedData();

        finalData.setData(barData);
        finalData.setData(getLineDataTime());

        combinedChart.setData(finalData);
        combinedChart.setDescription(new Description());
        combinedChart.invalidate();

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setAxisMinimum(200.f);
        leftAxis.setAxisMaximum(1800f);
        leftAxis.setTextColor(Color.WHITE);
        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setTextColor(Color.WHITE);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setSpaceMin(10);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.f);
        xAxis.setAxisMaximum(barData.getEntryCount()+1);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(barData.getEntryCount());
        ArrayList<String> strList = mHelper.getDailyXAxisData();
        xAxis.setValueFormatter(new DateXAxisValueFormatter(strList));
        Legend legend = combinedChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setTextColor(Color.WHITE);
    }

    // You must initialize mListCO before call it
    public BarData getBarDataCO() {
        if( mListCO.size() == 0 )
            return null;

        List<BarEntry> entries = new ArrayList<BarEntry>();

        int idx = 1;
        for ( int coData : mListCO ) {
            entries.add(new BarEntry(idx, coData));
            idx += 1;
        }
        BarDataSet dataSet = new BarDataSet(entries, "평균 CO 노출량(ppm)");
        dataSet.setValueTextSize(15f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setColor(Color.rgb(152, 218, 250));
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(dataSet);

        return barData;
    }

    // You must initialize mListTime before call it
    public LineData getLineDataTime() {
        List<Entry> entries = new ArrayList<Entry> ();

        int idx = 1;
        for ( float timeData : mListTime ) {
            entries.add(new Entry(idx, timeData));
            idx += 1;
        }
        LineDataSet dataSet = new LineDataSet(entries, "작업 시간(분)");
        dataSet.setValueTextSize(10.f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setColor(Color.rgb(255, 125, 165));
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineData lineData = new LineData(dataSet);

        return lineData;
    }
}
