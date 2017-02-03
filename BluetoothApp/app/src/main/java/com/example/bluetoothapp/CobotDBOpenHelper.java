package com.example.bluetoothapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    public ArrayList<String> getDailyXAxisData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DATE(date) FROM COData WHERE DATE(date) <= DATE(datetime('now', 'localtime')) "
                +"GROUP BY DATE(date) ORDER BY date LIMIT 8", null);

        ArrayList<String> strList = new ArrayList<String>();
        while( cursor.moveToNext() ) {
            strList.add(cursor.getString(0).substring(8, 10)+"일");
        }
        return strList;
    }

    public void insertSample() {
        this.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM COData");

        db = this.getWritableDatabase();
        insertSampleDataSeries(db, "2017-01-29 07:00:00");
        insertSampleDataSeries(db, "2017-01-30 07:00:00");
        insertSampleDataSeries(db, "2017-01-31 15:31:23");
        insertSampleDataSeries(db, "2017-02-01 14:22:23");
        insertSampleDataSeries(db, "2017-02-02 15:00:00");
        insertSampleDataSeries(db, "2017-02-03 15:00:00");
        insertSampleDataSeries(db, "2017-02-04 07:00:00");
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
        Cursor cursor = db.rawQuery("SELECT AVG(co) FROM COData WHERE DATE(date) <= DATE(datetime('now', 'localtime')) "
                +"GROUP BY DATE(date) ORDER BY date LIMIT 8", null);

        String result = "";

        List<Integer> intList = new ArrayList<Integer>();
        while( cursor.moveToNext() ) {
            intList.add(cursor.getInt(0));
        }
        return intList;
    }

    public List<Float> getDailyTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT strftime('%s',  MAX(date)) - strftime('%s', MIN(date)), MAX(date), MIN(date) FROM COData "
                +"WHERE DATE(date) <= DATE(datetime('now', 'localtime')) GROUP BY DATE(date) ORDER BY date LIMIT 8", null);

        List<Float> floatList = new ArrayList<Float>();
        while( cursor.moveToNext() ) {
            float timeDiff = cursor.getFloat(0);
            timeDiff /= 60;
            floatList.add(timeDiff);
            Log.i("TimeDiff", cursor.getString(2)+" / "+cursor.getString(1)+" : " + timeDiff);
        }

        return floatList;
    }
}
