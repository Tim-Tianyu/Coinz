package com.example.tim.coinz;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.tim.coinz.FeedReaderContract.FeedEntry;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoadActivity extends AppCompatActivity implements DownloadFileTask.AsyncResponse {
    public static FirebaseListener firebaseListener = new FirebaseListener();
    FeedReaderDbHelper mDbHelper;
    private static final String TAG = "LoadActivity";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mDbHelper = new FeedReaderDbHelper(LoadActivity.this);
        String userId = getIntent().getStringExtra("userId");
        firebaseListener.downloadData(LoadActivity.this, userId);
    }

    public void onCompleteDownloadFirebaseData() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedEntry.COLUMN_USER_ID,
                FeedEntry.COLUMN_USER_QUID,
                FeedEntry.COLUMN_USER_SHIL,
                FeedEntry.COLUMN_USER_PENY,
                FeedEntry.COLUMN_USER_DOLR,
                FeedEntry.COLUMN_USER_GOLD,
                FeedEntry.COLUMN_USER_QUID_RATE,
                FeedEntry.COLUMN_USER_SHIL_RATE,
                FeedEntry.COLUMN_USER_PENY_RATE,
                FeedEntry.COLUMN_USER_DOLR_RATE,
                FeedEntry.COLUMN_USER_LAST_ACTIVE,
                FeedEntry.COLUMN_USER_DISTANCE,
        };

        String selection = FeedEntry.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = { User.currentUser.getUserId() };
        String sortOrder = FeedEntry.COLUMN_USER_LAST_ACTIVE + " DESC";

        Cursor cursor = db.query(
                FeedEntry.TABLE_USER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        if (cursor.getCount() == 0) {
            // first time on this device
            //TODO new user
            Log.i(TAG, "new user");
        } else if (cursor.getCount() > 1) {
            // shouldn't be possible
            Log.w(TAG, "found more than one user");
            loadBankAndCoinList(cursor);
        } else {
            loadBankAndCoinList(cursor);
        }
    }

    private void loadBankAndCoinList(Cursor cursor) {
        assert (cursor.moveToNext());
        String date = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_USER_LAST_ACTIVE));
        if (isCurrentDate(date)){
            loadBankAndCoinListFromLocal(cursor);
        } else {
            loadBankAndCoinListFromRemote();
        }
    }

    private void loadBankAndCoinListFromLocal(Cursor userCursor){
        // TODO
    }

    private void loadBankAndCoinListFromRemote() {
        DownloadFileTask task = new DownloadFileTask(LoadActivity.this);
        task.execute("http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson");
    }

    // this will be called when the map finish downloaded
    @Override
    public void processFinish(String jsonString) {
        double rateShil, rateDolr, ratePenny, rateQuid;;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject rates = jsonObject.getJSONObject("rates");
            rateShil = rates.getDouble("SHIL");
            rateDolr = rates.getDouble("DOLR");
            ratePenny = rates.getDouble("PENY");
            rateQuid = rates.getDouble("QUID");
        } catch (JSONException e) {
            e.printStackTrace();
            rateShil = 1.0;
            rateDolr = 1.0;
            ratePenny = 1.0;
            rateQuid = 1.0;
        }

        // delete all coins from previous date
        deleteAllCoins();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String userId = User.currentUser.getUserId();
        FeatureCollection featureCollection = FeatureCollection.fromJson(jsonString);
        for (Feature feature : Objects.requireNonNull(featureCollection.features())){
            Geometry geometry = feature.geometry();
            Point point = (Point) geometry;
            JsonObject properties =  feature.properties();
            String currency = Objects.requireNonNull(properties).get("currency").getAsString();
            String symbol = Objects.requireNonNull(properties).get("marker-symbol").getAsString();
            String id = Objects.requireNonNull(properties).get("id").getAsString();
            Double value = Double.parseDouble(Objects.requireNonNull(properties).get("value").getAsString());

            if (point == null) throw new AssertionError();
            List<Double> coordinates = point.coordinates();
            LatLng position = new LatLng(coordinates.get(1), coordinates.get(0));
            Coin.coinsList.add(new Coin(id, value, currency, symbol, position));

            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_COIN_ID, id);
            values.put(FeedEntry.COLUMN_COIN_VALUE, value);
            values.put(FeedEntry.COLUMN_COIN_CURRENCY, Coin.currencyToDouble(Coin.generateCurrencyByName(currency)));
            values.put(FeedEntry.COLUMN_COIN_VALUE, value);
            values.put(FeedEntry.COLUMN_COIN_LAT, position.getLatitude());
            values.put(FeedEntry.COLUMN_COIN_LNG, position.getLongitude());
            values.put(FeedEntry.COLUMN_COIN_SYMBOL, symbol);
            values.put(FeedEntry.COLUMN_COIN_STATUS, false);
            values.put(FeedEntry.COLUMN_COIN_USER_ID, userId);
            db.insert(FeedEntry.TABLE_COIN, null, values);
        }

        Bank.theBank = new Bank(20, 0,rateDolr, rateQuid, rateShil, ratePenny, 5,10,10,10,10);

        // TODO write rate data into user
        Intent intent = new Intent(this, MapActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private void deleteAllCoins(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = FeedEntry.COLUMN_COIN_USER_ID + " LIKE ?";
        String[] selectionArgs = { User.currentUser.getUserId() };
        int deletedRows = db.delete(FeedEntry.TABLE_COIN, selection, selectionArgs);
        Log.i(TAG, String.format("Delete %s coins", deletedRows));
    }

    private boolean isCurrentDate(String date){
        // TODO compare
        return false;
    }
}


