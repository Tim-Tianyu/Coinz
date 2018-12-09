package com.example.tim.coinz;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    private int dailyLimit, dailyCoins;
    private double valueDOLR, valueQUID,valueSHIL,valuePENY,valueGold;
    private double rateDOLR, rateQUID,rateSHIL,ratePENY;

    static final int normalDailyLimit = 25;
    static Bank theBank = new Bank(normalDailyLimit,0,1,1,1,1,0,0,0,0,0);


    Bank (int dailyLimit, int dailyCoins, double rateDOLR, double ratePENY, double rateQUID, double rateSHIL,
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

    boolean saveCoin(Coin coin, Double bonus){
        if (dailyCoins >= dailyLimit) return false;

        switch (coin.getCurrency()){
            case PENY: valuePENY += bonus * coin.getValue();
                break;
            case QUID: valueQUID += bonus * coin.getValue();
                break;
            case DOLR: valueDOLR += bonus * coin.getValue();
                break;
            case SHIL: valueSHIL += bonus * coin.getValue();
                break;
            case UNKNOWN: return false;
        }
        dailyCoins++;
        syncWithLocal();
        return true;
    }

    public Map<Coin.Currency, Double> getValues(){
        HashMap<Coin.Currency, Double> map = new HashMap<>();
        map.put(Coin.Currency.DOLR, valueDOLR);
        map.put(Coin.Currency.QUID, valueQUID);
        map.put(Coin.Currency.SHIL, valueSHIL);
        map.put(Coin.Currency.PENY, valuePENY);
        return map;
    }

    Map<Coin.Currency, Double> getRates() {
        HashMap<Coin.Currency, Double> map = new HashMap<>();
        map.put(Coin.Currency.DOLR, rateDOLR);
        map.put(Coin.Currency.QUID, rateQUID);
        map.put(Coin.Currency.SHIL, rateSHIL);
        map.put(Coin.Currency.PENY, ratePENY);
        return map;
    }

    double getValueGold() {
        return valueGold;
    }

    boolean exchangeCurrenciesToGold(double valueDOLR, double valuePENY, double valueSHIL, double valueQUID){
        if (this.valueQUID < valueQUID || this.valuePENY < valuePENY || this.valueSHIL < valueSHIL || this.valueDOLR < valueDOLR) return false;
        this.valueGold += rateQUID * valueQUID + rateSHIL * valueSHIL + ratePENY * valuePENY + rateDOLR * valueDOLR;
        this.valueQUID -= valueQUID;
        this.valuePENY -= valuePENY;
        this.valueSHIL -= valueSHIL;
        this.valueDOLR -= valueDOLR;
        syncWithLocal();
        return true;
    }

    boolean exchangeGoldToCurrency(double valueGold, Coin.Currency currency){
        if (this.valueGold < valueGold) return false;
        this.valueGold -= valueGold;
        switch (currency){
            case PENY: valuePENY += valueGold / ratePENY;
                break;
            case QUID: valueQUID += valueGold / rateQUID;
                break;
            case DOLR: valueDOLR += valueGold / rateDOLR;
                break;
            case SHIL: valueSHIL += valueGold / rateSHIL;
                break;
            case UNKNOWN: return false;
        }
        syncWithLocal();
        return true;
    }

    void receiveGift(ReceiveGiftListAdapter current, Gift gift, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("GIFT").document(gift.getGiftId()).update("IsReceived", true).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                current.removeItem(position);
                valueGold += gift.getValue();
                syncWithLocal();
            } else {
                Log.w("BANK", "reveive gift fail");
            }
        });
    }

    void collectReward(Reward reward){
        valueGold += reward.getRewardValue();
        syncWithLocal();
    }

    private void syncWithLocal() {
        FeedReaderDbHelper mDbHelper = LoadActivity.mDbHelper;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_QUID, valueQUID);
        userValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_SHIL, valueSHIL);
        userValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_PENY, valuePENY);
        userValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_DOLR, valueDOLR);
        userValues.put(FeedReaderContract.FeedEntry.COLUMN_USER_GOLD, valueGold);

        String selectionUser = FeedReaderContract.FeedEntry.COLUMN_USER_ID + " LIKE ?";
        String[] selectionUserArgs = { User.currentUser.getUserId() };
        int countUser = db.update(
                FeedReaderContract.FeedEntry.TABLE_USER,
                userValues,
                selectionUser,
                selectionUserArgs);
        assert (countUser == 1);
        mDbHelper.close();
    }
}
