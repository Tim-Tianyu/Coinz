package com.example.tim.coinz;

import android.content.Context;
import android.content.Intent;

public class DownloadCompleteRunner {
    static String result;
    static Context current;

    public static void setCurrent(Context context){
        current = context;
    }

    public static void downloadComplete(String result) {
        DownloadCompleteRunner.result = result;
        Intent intent = new Intent(current, MapActivity.class);
        intent.putExtra("GEO_JSON", result);
        current.startActivity(intent);
    }
}
