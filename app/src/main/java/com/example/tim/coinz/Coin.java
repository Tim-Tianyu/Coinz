package com.example.tim.coinz;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.example.tim.coinz.FeedReaderContract.FeedEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Coin {
    static ArrayList<Coin> coinsList = new ArrayList<>();
    static ArrayList<Coin> collectedCoinsList = new ArrayList<>();
    private String id;
    private Currency currency;
    private double value;
    private Marker marker;
    private LatLng position;
    private String symbol;

    private static final int CURRENCY_UNKNOWN = -1;
    private static final int CURRENCY_DOLR = 0;
    private static final int CURRENCY_PENY = 1;
    private static final int CURRENCY_QUID = 2;
    private static final int CURRENCY_SHIL = 3;

    private static final String TAG = "Coin";

    public enum Currency {
        QUID, SHIL, PENY, DOLR, UNKNOWN
    }

    Coin (String id, double value, Currency currency, String symbol, LatLng position) {
        this.currency = currency;
        this.id = id;
        this.value = value;
        this.symbol = symbol;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    Currency getCurrency() {
        return currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public LatLng getPosition() {
        return position;
    }

    Marker getMarker() {
        return marker;
    }

    void setMarker(Marker marker) {
        this.marker = marker;
    }

    static void sendCoinAsGift(Dialog dialog, CoinListAdapter adapter, Coin coin, User friend){
        if (coin.currency.equals(Currency.UNKNOWN)) return;
        // create gift on cloud database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("USER");
        Double value = coin.value * Bank.theBank.getRates().get(coin.currency);
        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime());
        Map<String, Object> docData = new HashMap<>();
        docData.put("IsReceived", false);
        docData.put("Sender", userCollection.document(User.currentUser.getUserId()));
        docData.put("Receiver", userCollection.document(friend.getUserId()));
        docData.put("Value", value);
        docData.put("Time", timestamp);

        db.collection("GIFT").add(docData)
                .addOnSuccessListener(documentReference -> {
                    String giftId = documentReference.getId();
                    Gift.sentGifts.add(new Gift(giftId, value, false, User.currentUser.getUserId(), friend.getUserId(), timestamp));
                    dialog.dismiss();
                    adapter.removeCoin(coin);
                })
                .addOnFailureListener(e -> Log.w(TAG, e));
    }

    static int currencyToInt(Currency currency) {
        if (currency.equals(Currency.DOLR)) return CURRENCY_DOLR;
        else if (currency.equals(Currency.PENY)) return CURRENCY_PENY;
        else if (currency.equals(Currency.QUID)) return CURRENCY_QUID;
        else if (currency.equals(Currency.SHIL)) return CURRENCY_SHIL;
        else return CURRENCY_UNKNOWN;
    }

    static Currency intToCurrency(int num) {
        if (num == CURRENCY_DOLR) return Currency.DOLR;
        else if (num == CURRENCY_PENY) return Currency.PENY;
        else if (num == CURRENCY_QUID) return Currency.QUID;
        else if (num == CURRENCY_SHIL) return Currency.SHIL;
        else return Currency.UNKNOWN;
    }

    static Coin getCoinByMarker(Marker marker) {
        for (Coin coin: coinsList) {
            if (coin.marker == marker) return coin;
        }
        return null;
    }

    static boolean inRanged(Location location, Coin coin) {
        // collecting range
        double range = 25;
        return coin.getPosition().distanceTo(new LatLng(location.getLatitude(),location.getLongitude())) < range;
    }

    static boolean inViewRange(Location location, Coin coin){
        // view range (used in treasure hunt mode
        double range = 100;
        return coin.getPosition().distanceTo(new LatLng(location.getLatitude(),location.getLongitude())) < range;
    }

    static Currency generateCurrencyByName(String name) {
        switch (name) {
            case "DOLR":
                return Currency.DOLR;
            case "QUID":
                return Currency.QUID;
            case "PENY":
                return Currency.PENY;
            case "SHIL":
                return Currency.SHIL;
            default:
                return Currency.UNKNOWN;
        }
    }

    static void collectCoin(Coin coin){
        // update local db
        SQLiteDatabase db = LoadActivity.mDbHelper.getWritableDatabase();
        ContentValues coinValues = new ContentValues();
        coinValues.put(FeedEntry.COLUMN_COIN_STATUS, true);
        String selectionCoin = FeedEntry.COLUMN_COIN_ID + " LIKE ?";
        String[] selectionCoinArgs = { coin.getId() };
        int countCoin = db.update(
                FeedEntry.TABLE_COIN,
                coinValues,
                selectionCoin,
                selectionCoinArgs);
        if (countCoin == 1) {
            Log.w(TAG, "multiple coin with same Id");
        }
        collectedCoinsList.add(coin);
        coinsList.remove(coin);
        LoadActivity.mDbHelper.close();
    }

    static void discardCoin(Coin coin) {
        // remove coin in local db
        SQLiteDatabase db = LoadActivity.mDbHelper.getWritableDatabase();
        String selectionCoin = FeedEntry.COLUMN_COIN_ID + " LIKE ?";
        String[] selectionCoinArgs = { coin.getId() };
        int countCoin = db.delete(
                FeedEntry.TABLE_COIN,
                selectionCoin,
                selectionCoinArgs);
        if (countCoin != 1) {
            Log.w(TAG, "multiple coin with same Id");
        }
        collectedCoinsList.remove(coin);
        LoadActivity.mDbHelper.close();
    }
}
