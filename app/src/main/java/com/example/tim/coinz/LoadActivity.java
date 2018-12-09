package com.example.tim.coinz;

import android.app.AlertDialog;
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
    // activity to load all the local and cloud data
    public static FirebaseListener firebaseListener = new FirebaseListener();
    static FeedReaderDbHelper mDbHelper;
    private static final String TAG = "LoadActivity";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    private Double valueDolr = 0.0 ,valuePeny = 0.0, valueQuid = 0.0, valueShil = 0.0, valueGold = 0.0;
    private boolean gameModeSelected = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        mDbHelper = new FeedReaderDbHelper(LoadActivity.this);
        userId = getIntent().getStringExtra("userId");
    }

    @Override
    protected void onStart() {
        // first download firebase data
        firebaseListener.downloadData(LoadActivity.this, userId);
        super.onStart();
    }

    public void onCompleteDownloadFirebaseData() {
        // then load user data
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
                FeedEntry.COLUMN_USER_MODE,
                FeedEntry.COLUMN_USER_IS_SELECT,
                FeedEntry.COLUMN_USER_DISTANCE,
                FeedEntry.COLUMN_USER_REWARD_LEVEL,
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
                // same day from last download
                User.walkingDistance = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_DISTANCE));
                gameModeSelected = userCursor.getInt(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_IS_SELECT)) != 0;
                MapActivity.selectedMode = userCursor.getInt(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_MODE)) != 0;
                Reward.currentLevel = userCursor.getInt(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_REWARD_LEVEL));
                loadBankAndCoinListFromLocal(userCursor);
                userCursor.close();
                if (gameModeSelected) {
                    this.startActivity(new Intent(this, MapActivity.class));
                } else {
                    this.startActivity(new Intent(this, gameSelectActivity.class));
                }
            } else {
                // different date from last download
                userCursor.close();
                loadBankAndCoinListFromRemote();
            }
        }
    }

    private void loadBankAndCoinListFromLocal(Cursor userCursor){
        // load local coins data and bank exchange rate
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
        // coins data
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

        // bank exchange rate
        Double rateDolr = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_DOLR_RATE));
        Double ratePenny = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_PENY_RATE));
        Double rateQuid = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_QUID_RATE));
        Double rateShil = userCursor.getDouble(userCursor.getColumnIndex(FeedEntry.COLUMN_USER_SHIL_RATE));
        Bank.theBank = new Bank(Bank.normalDailyLimit, dailyCoins, rateDolr, ratePenny, rateQuid, rateShil, valueGold, valueDolr, valuePeny, valueQuid, valueShil);
    }

    private void loadBankAndCoinListFromRemote(){
        // download geo-json
        DownloadFileTask task = new DownloadFileTask(LoadActivity.this);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        String URL;
        if (day < 10) {
            URL = String.format("http://homepages.inf.ed.ac.uk/stg/coinz/%s/%s/0%s/coinzmap.geojson", year, month, day);
        } else {
            URL = String.format("http://homepages.inf.ed.ac.uk/stg/coinz/%s/%s/%s/coinzmap.geojson", year, month, day);
        }
        task.execute(URL);
    }

    // this will be called when the map finish downloaded (after loadBankAndCoinListFromRemote);
    @Override
    public void processFinish(String jsonString) {
        // load coins and bank exchange rate form geo-json
        double rateShil, rateDolr, ratePenny, rateQuid;

        // empty string will be returned if network issue happened (IO exception in downloadFileTask)
        if (jsonString.isEmpty()) {
            // dialog to report network issue
            AlertDialog.Builder builder = new AlertDialog.Builder(LoadActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Network issue")
                    .setMessage("Experiencing network issue, try to reconnect?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> loadBankAndCoinListFromRemote())
                    .setNegativeButton(android.R.string.no, (dialog, which) ->{
                        dialog.dismiss();
                        finish();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .create().show();
            return;
        }

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
        // use FeatureCollection to get coin data for json file
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
        values.put(FeedEntry.COLUMN_USER_IS_SELECT, false);
        values.put(FeedEntry.COLUMN_USER_REWARD_LEVEL, Reward.LEVEL_0);
        String selection = FeedEntry.COLUMN_USER_ID + " LIKE ?";
        String[] selectionArgs = { userId };
        int count = db.update(FeedEntry.TABLE_USER, values, selection, selectionArgs);
        assert (count==1);

        // load currency values form user
        Bank.theBank = new Bank(Bank.normalDailyLimit, 0,rateDolr, ratePenny, rateQuid, rateShil, valueGold, valueDolr, valuePeny, valueQuid, valueShil);

        if (gameModeSelected) {
            this.startActivity(new Intent(this, MapActivity.class));
        } else {
            this.startActivity(new Intent(this, gameSelectActivity.class));
        }
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
        values.put(FeedEntry.COLUMN_USER_LAST_ACTIVE, "");
        values.put(FeedEntry.COLUMN_USER_MODE, true);
        values.put(FeedEntry.COLUMN_USER_IS_SELECT, false);
        values.put(FeedEntry.COLUMN_USER_DISTANCE, 0.0);
        values.put(FeedEntry.COLUMN_USER_REWARD_LEVEL, Reward.LEVEL_0);
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
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


