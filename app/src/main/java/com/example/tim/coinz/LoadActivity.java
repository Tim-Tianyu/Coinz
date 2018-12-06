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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoadActivity extends AppCompatActivity implements DownloadFileTask.AsyncResponse {
    public static FirebaseListener firebaseListener = new FirebaseListener();
    static FeedReaderDbHelper mDbHelper;
    private static final String TAG = "LoadActivity";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    private Double valueDolr = 0.0 ,valuePeny = 0.0, valueQuid = 0.0, valueShil = 0.0, valueGold = 0.0;

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

        Cursor userCursor = db.query(
                FeedEntry.TABLE_USER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        Coin.collectedCoinsList = new ArrayList<>();
        Coin.coinsList = new ArrayList<>();

        if (userCursor.getCount() == 0) {
            // first time on this device
            Log.i(TAG, "new user on this device");
            createNewUser();
            loadBankAndCoinListFromRemote();
        } else {
            assert (userCursor.getCount() == 1);
            Boolean success = userCursor.moveToNext();
            assert (success);
            String date = userCursor.getString(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_LAST_ACTIVE));
            // value for each currency will be used in creating bank object later
            valueDolr = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_DOLR));
            valuePeny = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_PENY));
            valueQuid = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_QUID));
            valueShil = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_SHIL));
            valueGold = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_GOLD));
            if (isCurrentDate(date)){
                User.walkingDistance = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_DISTANCE));
                loadBankAndCoinListFromLocal(userCursor);
                userCursor.close();
            } else {
                userCursor.close();
                loadBankAndCoinListFromRemote();
            }
        }
        this.startActivity(new Intent(this, MapActivity.class));
        this.finish();
    }

    private void loadBankAndCoinListFromLocal(Cursor userCursor){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedEntry.COLUMN_COIN_ID,
                FeedEntry.COLUMN_COIN_CURRENCY,
                FeedEntry.COLUMN_COIN_VALUE,
                FeedEntry.COLUMN_COIN_LAT,
                FeedEntry.COLUMN_COIN_LNG,
                FeedEntry.COLUMN_COIN_SYMBOL,
                FeedEntry.COLUMN_COIN_STATUS,
                FeedEntry.COLUMN_COIN_USER_ID,
        };
        String selection = FeedEntry.COLUMN_COIN_USER_ID + " = ?";
        String[] selectionArgs = { User.currentUser.getUserId() };
        String sortOrder = FeedEntry.COLUMN_COIN_ID + " DESC";

        Cursor coinCursor = db.query(
                FeedEntry.TABLE_COIN,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        assert (coinCursor.getCount() == 50);
        int dailyCoins = 0;
        while (coinCursor.moveToNext()){
            String id = coinCursor.getString(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_ID));
            Double value = coinCursor.getDouble(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_VALUE));
            Coin.Currency currency = Coin.intToCurrency(coinCursor.getInt(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_CURRENCY)));
            String symbol = coinCursor.getString(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_SYMBOL));
            Double Lat = coinCursor.getDouble(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_LAT));
            Double Lng = coinCursor.getDouble(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_LNG));
            LatLng position = new LatLng(Lat,Lng);
            Boolean hasCollected = coinCursor.getInt(coinCursor.getColumnIndex(FeedEntry.COLUMN_COIN_STATUS)) != 0;
            if (hasCollected){
                Coin.collectedCoinsList.add(new Coin(id, value, currency, symbol, position));
                dailyCoins++;
            } else {
                Coin.coinsList.add(new Coin(id, value, currency, symbol, position));
            }
        }
        coinCursor.close();
        Double rateDolr = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_DOLR_RATE));
        Double ratePenny = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_PENY_RATE));
        Double rateQuid = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_QUID_RATE));
        Double rateShil = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_SHIL_RATE));
        Bank.theBank = new Bank(Bank.normalDailyLimit, dailyCoins, rateDolr, ratePenny, rateQuid, rateShil, valueGold, valueDolr, valuePeny, valueQuid, valueShil);
    }

    private void loadBankAndCoinListFromRemote() {
        DownloadFileTask task = new DownloadFileTask(LoadActivity.this);
        task.execute("http://homepages.inf.ed.ac.uk/stg/coinz/2018/10/03/coinzmap.geojson");
    }

    // this will be called when the map finish downloaded (after loadBankAndCoinListFromRemote);
    @Override
    public void processFinish(String jsonString) {
        double rateShil, rateDolr, ratePenny, rateQuid;
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
            Coin.Currency currency = Coin.generateCurrencyByName(Objects.requireNonNull(properties).get("currency").getAsString());
            String symbol = Objects.requireNonNull(properties).get("marker-symbol").getAsString();
            String id = Objects.requireNonNull(properties).get("id").getAsString();
            Double value = Double.parseDouble(Objects.requireNonNull(properties).get("value").getAsString());

            if (point == null) throw new AssertionError();
            List<Double> coordinates = point.coordinates();
            LatLng position = new LatLng(coordinates.get(1), coordinates.get(0));

            Coin.coinsList.add(new Coin(id, value, currency, symbol, position));

            // add coin into database
            ContentValues values = new ContentValues();
            values.put(FeedEntry.COLUMN_COIN_ID, id);
            values.put(FeedEntry.COLUMN_COIN_CURRENCY, Coin.currencyToInt(currency));
            values.put(FeedEntry.COLUMN_COIN_VALUE, value);
            values.put(FeedEntry.COLUMN_COIN_LAT, position.getLatitude());
            values.put(FeedEntry.COLUMN_COIN_LNG, position.getLongitude());
            values.put(FeedEntry.COLUMN_COIN_SYMBOL, symbol);
            values.put(FeedEntry.COLUMN_COIN_STATUS, false);
            values.put(FeedEntry.COLUMN_COIN_USER_ID, userId);
            long success = db.insert(FeedEntry.TABLE_COIN, null, values);
            assert (success != -1);
        }

        // update bank rate, last active date, walking distance for user
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_USER_QUID_RATE, rateQuid);
        values.put(FeedEntry.COLUMN_USER_SHIL_RATE, rateShil);
        values.put(FeedEntry.COLUMN_USER_PENY_RATE, ratePenny);
        values.put(FeedEntry.COLUMN_USER_DOLR_RATE, rateDolr);
        values.put(FeedEntry.COLUMN_USER_LAST_ACTIVE, currentDate());
        values.put(FeedEntry.COLUMN_USER_DISTANCE, 0.0);
        String selection = FeedEntry.COLUMN_USER_ID + " LIKE ?";
        String[] selectionArgs = { userId };
        int count = db.update(FeedEntry.TABLE_USER, values, selection, selectionArgs);
        assert (count==1);

        // load currency values form user
        Bank.theBank = new Bank(Bank.normalDailyLimit, 0,rateDolr, ratePenny, rateQuid, rateShil, valueGold, valueDolr, valuePeny, valueQuid, valueShil);
    }

    private void createNewUser() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_USER_ID, User.currentUser.getUserId());
        values.put(FeedEntry.COLUMN_USER_QUID, 0.0);
        values.put(FeedEntry.COLUMN_USER_SHIL, 0.0);
        values.put(FeedEntry.COLUMN_USER_PENY, 0.0);
        values.put(FeedEntry.COLUMN_USER_DOLR, 0.0);
        values.put(FeedEntry.COLUMN_USER_QUID_RATE, 1.0);
        values.put(FeedEntry.COLUMN_USER_SHIL_RATE, 1.0);
        values.put(FeedEntry.COLUMN_USER_PENY_RATE, 1.0);
        values.put(FeedEntry.COLUMN_USER_DOLR_RATE, 1.0);
        values.put(FeedEntry.COLUMN_USER_GOLD, 0.0);
        values.put(FeedEntry.COLUMN_USER_LAST_ACTIVE, currentDate());
        values.put(FeedEntry.COLUMN_USER_DISTANCE, 0.0);
        long success = db.insert(FeedEntry.TABLE_USER, null, values);
        assert (success != -1);
    }

    private void deleteAllCoins(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = FeedEntry.COLUMN_COIN_USER_ID + " LIKE ?";
        String[] selectionArgs = { User.currentUser.getUserId() };
        int deletedRows = db.delete(FeedEntry.TABLE_COIN, selection, selectionArgs);
        assert (deletedRows != 0);
        Log.i(TAG, String.format("Delete %s coins", deletedRows));
    }

    private boolean isCurrentDate(String date){
        return (date.equals(sdf.format(Calendar.getInstance().getTime())));
    }

    private String currentDate() {
        return sdf.format(Calendar.getInstance().getTime());
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


