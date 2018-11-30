package com.example.tim.coinz;

import android.location.Location;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

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
        QUID, SHIL, PENNY, DOLR, UNKNOWN
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
            case "PENNY":
                return currencies.PENNY;
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
}
