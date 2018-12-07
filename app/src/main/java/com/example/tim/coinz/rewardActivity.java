package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class rewardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        TextView txtDistance = findViewById(R.id.activity_reward_txt_distance);
        txtDistance.setText(String.format(Locale.UK, "Daily walking distance: %1$.2f", User.walkingDistance));
    }
}
