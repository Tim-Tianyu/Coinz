package com.example.tim.coinz;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class gameSelectActivity extends AppCompatActivity {
    // Activity to choose daily game mode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);
        Intent intent = new Intent(gameSelectActivity.this, MapActivity.class);
        Button btnNormal = findViewById(R.id.activity_game_select_btn_normal);
        btnNormal.setOnClickListener(v -> {
            updateLocalGameMode(MapActivity.NORMAL);
            MapActivity.selectedMode = MapActivity.NORMAL;
            startActivity(intent);
            finish();
        });
        Button btnHunt = findViewById(R.id.activity_game_select_btn_hunt);
        btnHunt.setOnClickListener(v -> {
            updateLocalGameMode(MapActivity.TREASURE_HUNT);
            MapActivity.selectedMode = MapActivity.TREASURE_HUNT;
            startActivity(intent);
            finish();
        });
    }

    private void updateLocalGameMode(boolean mode){
        SQLiteDatabase db = LoadActivity.mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_USER_MODE, mode);
        values.put(FeedReaderContract.FeedEntry.COLUMN_USER_IS_SELECT, true);
        String selection = FeedReaderContract.FeedEntry.COLUMN_USER_ID + " LIKE ?";
        String[] selectionArgs = { User.currentUser.getUserId() };
        db.update(FeedReaderContract.FeedEntry.TABLE_USER, values, selection, selectionArgs);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
