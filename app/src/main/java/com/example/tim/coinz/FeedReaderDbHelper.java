package com.example.tim.coinz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tim.coinz.FeedReaderContract.FeedEntry;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Local.db";
    private static final String SQL_CREATE_TABLE_COIN =
            "CREATE TABLE " + FeedEntry.TABLE_COIN + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_COIN_ID + " VARCHAR(255)," +
                    FeedEntry.COLUMN_COIN_CURRENCY + " TINYINT," +
                    FeedEntry.COLUMN_COIN_VALUE + " DOUBLE," +
                    FeedEntry.COLUMN_COIN_LAT + " DOUBLE," +
                    FeedEntry.COLUMN_COIN_LNG + " DOUBLE," +
                    FeedEntry.COLUMN_COIN_SYMBOL + " CHARACTER(20)," +
                    FeedEntry.COLUMN_COIN_STATUS + " BOOLEAN," +
                    FeedEntry.COLUMN_COIN_USER_ID + " VARCHAR(255))";

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + FeedEntry.TABLE_USER + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_USER_ID + " VARCHAR(255)," +
                    FeedEntry.COLUMN_USER_QUID + " DOUBLE," +
                    FeedEntry.COLUMN_USER_SHIL + " DOUBLE," +
                    FeedEntry.COLUMN_USER_PENY + " DOUBLE," +
                    FeedEntry.COLUMN_USER_DOLR + " DOUBLE," +
                    FeedEntry.COLUMN_USER_QUID_RATE + " DOUBLE," +
                    FeedEntry.COLUMN_USER_SHIL_RATE + " DOUBLE," +
                    FeedEntry.COLUMN_USER_PENY_RATE + " DOUBLE," +
                    FeedEntry.COLUMN_USER_DOLR_RATE + " DOUBLE," +
                    FeedEntry.COLUMN_USER_GOLD + " DOUBLE," +
                    FeedEntry.COLUMN_USER_LAST_ACTIVE + "DATE" +
                    FeedEntry.COLUMN_USER_DISTANCE + " DOUBLE)";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME + ".db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_COIN);
        db.execSQL(SQL_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no upgrade plan
    }
}
