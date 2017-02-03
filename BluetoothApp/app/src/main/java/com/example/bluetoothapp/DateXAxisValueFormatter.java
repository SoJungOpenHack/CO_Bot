package com.example.bluetoothapp;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

/**
 * Created by Test on 2017-02-04.
 */

public class DateXAxisValueFormatter implements IAxisValueFormatter {

    private ArrayList<String> mStrList;

    public DateXAxisValueFormatter(ArrayList<String> strList) {
        mStrList = strList;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Log.i("value", ""+value);
        int idx = (int)value-1;
        if( 0 <= idx && idx < mStrList.size() )
            return mStrList.get(idx);
        else
            return "";
    }
}
