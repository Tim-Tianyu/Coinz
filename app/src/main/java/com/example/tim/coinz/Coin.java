package com.example.tim.coinz;

import android.app.Application;
import android.app.Dialog;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Coin {
    static ArrayList<Coin> coinsList = new ArrayList<>();
    static ArrayList<Coin> collectedCoinsList = new ArrayList<>();
    private String id;
    private currencies currency;
    private double value;
    private Marker marker;
    private LatLng position;
    private String symbol;

    public enum currencies {
        QUID, SHIL, PENY, DOLR, UNKNOWN
    }

    public static Coin getCoinByMarker(Marker marker) {
        for (Coin coin: coinsList){
            if (coin.marker == marker) return coin;
        }
        return null;
    }

    public static boolean inRanged(Location location, Coin coin){
        double range = 50;
        return coin.marker.getPosition().distanceTo(new LatLng(location.getLatitude(),location.getLongitude())) < range;
    }

    public static currencies generateCurrencyByName(String name){
        switch (name) {
            case "DOLR":
                return currencies.DOLR;
            case "QUID":
                return currencies.QUID;
            case "PENY":
                return currencies.PENY;
            case "SHIL":
                return currencies.SHIL;
            default:
                return currencies.UNKNOWN;
        }
    }

    public Coin (String id, double value, String currency, String symbol, LatLng position) {
        this.currency = Coin.generateCurrencyByName(currency);
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

    public currencies getCurrency() {
        return currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    static void sendCoinAsGift(Dialog dialog, Coin coin, User friend, int position){
        if (coin.currency.equals(currencies.UNKNOWN)) return;
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
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String giftId = documentReference.getId();
                        Gift.sentGifts.add(new Gift(giftId, value, false, User.currentUser.getUserId(), friend.getUserId(), timestamp));
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO
                    }
                });
    }
}
