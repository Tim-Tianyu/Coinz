package com.example.tim.coinz;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    public static Bank theBank = new Bank(10,0,1,1,1,1,0,0,0,0,0);

    private int dailyLimit, dailyCoins;
    private double valueDOLR, valueQUID,valueSHIL,valuePENY,valueGold;
    private double rateDOLR, rateQUID,rateSHIL,ratePENY;

    public Bank (int dailyLimit, int dailyCoins, double rateDOLR, double ratePENY, double rateQUID, double rateSHIL,
                 double valueGold, double valueDOLR, double valuePENY, double valueQUID, double valueSHIL) {
        this.dailyLimit = dailyLimit;
        this.dailyCoins = dailyCoins;
        this.valueDOLR = valueDOLR;
        this.valueQUID = valueQUID;
        this.valueSHIL = valueSHIL;
        this.valuePENY = valuePENY;
        this.valueGold = valueGold;
        this.rateDOLR = rateDOLR;
        this.rateQUID = rateQUID;
        this.rateSHIL = rateSHIL;
        this.ratePENY = ratePENY;
    }

    public boolean saveCoin(Coin coin){
        if (dailyCoins >= dailyLimit) return false;

        switch (coin.getCurrency()){
            case PENNY: valuePENY += coin.getValue();
                break;
            case QUID: valueQUID += coin.getValue();
                break;
            case DOLR: valueDOLR += coin.getValue();
                break;
            case SHIL: valueSHIL += coin.getValue();
                break;
            case UNKNOWN: return false;
        }
        dailyCoins++;
        return true;
    }

    public Map<Coin.currencies, Double> getValues(){
        HashMap<Coin.currencies, Double> map = new HashMap<>();
        map.put(Coin.currencies.DOLR, valueDOLR);
        map.put(Coin.currencies.QUID, valueQUID);
        map.put(Coin.currencies.SHIL, valueSHIL);
        map.put(Coin.currencies.PENNY, valuePENY);
        return map;
    }

    public Map<Coin.currencies, Double> getRates() {
        HashMap<Coin.currencies, Double> map = new HashMap<>();
        map.put(Coin.currencies.DOLR, rateDOLR);
        map.put(Coin.currencies.QUID, rateQUID);
        map.put(Coin.currencies.SHIL, rateSHIL);
        map.put(Coin.currencies.PENNY, ratePENY);
        return map;
    }

    public double getValueGold() {
        return valueGold;
    }

    public boolean exchangeCurrenciesToGold(double valueDOLR, double valuePENY, double valueSHIL, double valueQUID){
        if (this.valueQUID < valueQUID || this.valuePENY < valuePENY || this.valueSHIL < valueSHIL || this.valueDOLR < valueDOLR) return false;
        this.valueGold += rateQUID * valueQUID + rateSHIL * valueSHIL + ratePENY * valuePENY + rateDOLR * valueDOLR;
        this.valueQUID -= valueQUID;
        this.valuePENY -= valuePENY;
        this.valueSHIL -= valueSHIL;
        this.valueDOLR -= valueDOLR;
        return true;
    }

    public boolean exchangeGoldToCurrency(double valueGold, Coin.currencies currency){
        if (this.valueGold < valueGold) return false;
        this.valueGold -= valueGold;
        switch (currency){
            case PENNY: valuePENY += valueGold / ratePENY;
                break;
            case QUID: valueQUID += valueGold / rateQUID;
                break;
            case DOLR: valueDOLR += valueGold / rateDOLR;
                break;
            case SHIL: valueSHIL += valueGold / rateSHIL;
                break;
            case UNKNOWN: return false;
        }
        return true;
    }
}
