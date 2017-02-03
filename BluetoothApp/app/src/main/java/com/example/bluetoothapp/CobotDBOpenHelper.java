package com.example.bluetoothapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Test on 2017-02-03.
 */

public class CobotDBOpenHelper extends SQLiteOpenHelper {
    CobotDBOpenHelper(Context context) {
        super(context, "CobotDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS COData(" +
                "co INT, " +
                "date TEXT);"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void insertSampleDataSeries(SQLiteDatabase db, String date) {
        int startVal = 400;
        int increaseTime = 0;
        for(int i=0; i<15; i++) {
            startVal += Math.floor(Math.random()*80)+50;
            increaseTime += Math.floor(Math.random()*60)+20;

            db.execSQL("INSERT INTO COData VALUES("+startVal+", datetime('"+date+"', '+"+increaseTime+" second'))");
        }
        for(int i=0; i<15; i++) {
            startVal -= Math.floor(Math.random()*80)+50;
            increaseTime += Math.floor(Math.random()*60)+20;

            db.execSQL("INSERT INTO COData VALUES("+startVal+", datetime('"+date+"', '+"+increaseTime+" second'))");
        }
    }

    public void insertSample() {
        this.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COData");

        db = this.getWritableDatabase();
        insertSampleDataSeries(db, "2017-01-31 15:31:23");
        insertSampleDataSeries(db, "2017-02-01 14:22:23");
        insertSampleDataSeries(db, "2017-02-02 15:00:00");
        insertSampleDataSeries(db, "2017-02-03 15:00:00");
    }

    public void insertRow(int ppmCO) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO COData VALUES("+ppmCO+", datetime('now', 'localtime'))");
    }

    public void getAverageDate() {
    }

    public String getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM COData", null);

        String result = "";

        while( cursor.moveToNext() ) {
            result += cursor.getString(0) + " / " + cursor.getString(1)+ "\n";
        }
        return result;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COData");
    }

    public List<Integer> getDailyAverage() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(co) FROM COData GROUP BY date", null);

        String result = "";

        List<Integer> intList = new ArrayList<Integer>();
        while( cursor.moveToNext() ) {
            intList.add(cursor.getInt(0));
        }
        return intList;
    }

    public String getDailyTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT strftime('%s',  MAX(date)) - strftime('%s', MIN(date)) FROM COData GROUP BY DATE(date)", null);

        String result = "";

        while( cursor.moveToNext() ) {
            int secDiff = cursor.getInt(0);
            int minDiff = secDiff/60;
            int hourDiff = minDiff/60;
            minDiff %= 60;
            secDiff %= 60;

            if( hourDiff != 0 )
                result += hourDiff+"시간 ";
            if( minDiff != 0 )
                result += minDiff+"분 ";
            if( secDiff != 0 )
                result += secDiff+"초 ";
            if( hourDiff != 0 || minDiff != 0 || secDiff != 0 )
                result += "작업했습니다.\n";
        }

        return result;
    }
}
