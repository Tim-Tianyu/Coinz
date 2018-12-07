package com.example.tim.coinz;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.tim.coinz.FeedReaderContract.FeedEntry;

class Reward {
    static final int LEVEL_0 = 0;
    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_4 = 4;
    private static final int LEVEL_5 = 5;
    private static final int LEVEL_6 = 6;
    static final int LEVEL_END = 7;
    static int currentLevel = 0;

    private static final Reward[] rewardLevels = {
            new Reward(0.0, 100.0),
            new Reward(100.0, 200.0),
            new Reward(200.0, 200.0),
            new Reward(500.0, 300.0),
            new Reward(1000.0, 400.0),
            new Reward(2000.0, 700.0),
            new Reward(5000.0, 1000.0),
    };

    private Double thresholdDistance;
    private Double rewardValue;

    private Reward (Double thresholdDistance, Double rewardValue){
        this.thresholdDistance = thresholdDistance;
        this.rewardValue = rewardValue;
    }

    Double getRewardValue() {
        return rewardValue;
    }

    Double getThresholdDistance() {
        return thresholdDistance;
    }

    static void nextLevel(){
        int level = currentLevel;
        if (level == LEVEL_0) currentLevel = LEVEL_1;
        else if (level == LEVEL_1) currentLevel = LEVEL_2;
        else if (level == LEVEL_2) currentLevel = LEVEL_3;
        else if (level == LEVEL_3) currentLevel = LEVEL_4;
        else if (level == LEVEL_4) currentLevel = LEVEL_5;
        else if (level == LEVEL_5) currentLevel = LEVEL_6;
        else if (level == LEVEL_6) currentLevel = LEVEL_END;

        // sync database
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_USER_REWARD_LEVEL, currentLevel);
        String selection = FeedEntry.COLUMN_USER_ID + " LIKE ?";
        String[] selectionArgs = { User.currentUser.getUserId() };
        SQLiteDatabase db = LoadActivity.mDbHelper.getWritableDatabase();
        db.update(FeedReaderContract.FeedEntry.TABLE_USER, values, selection, selectionArgs);
    }

    static Reward getCurrentReward(){
        if (currentLevel == LEVEL_END) return null;
        return rewardLevels[currentLevel];
    }
}
