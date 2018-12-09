package com.example.tim.coinz;

import android.provider.BaseColumns;

final class FeedReaderContract {
    // define names used for tables and entries
    private FeedReaderContract() {}

    static class FeedEntry implements BaseColumns {
        static final String TABLE_COIN = "coin";
        static final String COLUMN_COIN_ID = "coinId";
        static final String COLUMN_COIN_CURRENCY = "currency";
        static final String COLUMN_COIN_VALUE = "value";
        static final String COLUMN_COIN_LAT = "lat";
        static final String COLUMN_COIN_LNG = "lng";
        static final String COLUMN_COIN_SYMBOL = "symbol";
        static final String COLUMN_COIN_STATUS = "status";
        static final String COLUMN_COIN_USER_ID = "userId";

        static final String TABLE_USER = "user";
        static final String COLUMN_USER_ID = "userId";
        static final String COLUMN_USER_QUID = "quid";
        static final String COLUMN_USER_SHIL = "shil";
        static final String COLUMN_USER_PENY = "peny";
        static final String COLUMN_USER_DOLR = "dolr";
        static final String COLUMN_USER_QUID_RATE = "quid_rate";
        static final String COLUMN_USER_SHIL_RATE = "shil_rate";
        static final String COLUMN_USER_PENY_RATE = "peny_rate";
        static final String COLUMN_USER_DOLR_RATE = "dolr_rate";
        static final String COLUMN_USER_GOLD = "gold";
        static final String COLUMN_USER_LAST_ACTIVE = "lastActive";
        static final String COLUMN_USER_DISTANCE = "distance";
        static final String COLUMN_USER_MODE = "selectedMode";
        static final String COLUMN_USER_IS_SELECT = "isSelect";
        static final String COLUMN_USER_REWARD_LEVEL = "level";
    }
}