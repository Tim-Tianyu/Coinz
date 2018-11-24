package com.example.tim.coinz;

import android.location.Location;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class Coin {
    static ArrayList<Coin> coinsList = new ArrayList<>();
    static ArrayList<Coin> collectedCoinsList = new ArrayList<>();
    static double range = 50;

    public enum currencies {
        QUID, SHIL, PENNY, DOLR, UNKNOWN
    }

    public static Coin getCoinByMarker(Marker marker) {
        for (Coin coin: coinsList){
            if (coin.marker == marker) return coin;
        }
        return null;
    }

    public static boolean inRanged(Location location, Coin coin){
        return coin.marker.getPosition().distanceTo(new LatLng(location.getLatitude(),location.getLongitude())) < range;
    }

    public static currencies generateCurrencyByName(String name){
        switch (name) {
            case "DOLR":
                return currencies.DOLR;
            case "QUID":
                return currencies.QUID;
            case "PENNY":
                return currencies.PENNY;
            case "SHIL":
                return currencies.SHIL;
            default:
                return currencies.UNKNOWN;
        }
    }

    private String id;
    private currencies currency;
    private double value;
    private Marker marker;


    public Coin (String id, currencies currency , double value, Marker marker) {
        this.currency = currency;
        this.id = id;
        this.value = value;
        this.marker = marker;
    }

    public currencies getCurrency() {
        return currency;
    }

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }
}
