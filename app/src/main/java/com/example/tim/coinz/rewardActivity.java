package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class rewardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        TextView txtDistance = findViewById(R.id.activity_reward_txt_distance);
        txtDistance.setText(String.format(Locale.UK, "Daily walking distance: %1$.2f", User.walkingDistance));
        Button btnCollect = findViewById(R.id.activity_reward_btn_collect);
        btnCollect.setOnClickListener(v -> {
            // try to collect reward
            if (Reward.currentLevel == Reward.LEVEL_END) {
                Toast.makeText(rewardActivity.this, "No more rewards to collect", Toast.LENGTH_SHORT).show();
                return;
            }
            Reward currentReward = Reward.getCurrentReward();
            if (Objects.requireNonNull(currentReward).getThresholdDistance() > User.walkingDistance) {
                Toast.makeText(rewardActivity.this, "Not reaching the target distance", Toast.LENGTH_SHORT).show();
            } else {
                // success collect reward
                Bank.theBank.collectReward(currentReward);
                Toast.makeText(rewardActivity.this, String.format(Locale.UK, "Collect %1$.2f gold", currentReward.getRewardValue()), Toast.LENGTH_SHORT).show();
                Reward.nextLevel();
                refreshExplanationText();
            }
        });
    }

    @Override
    protected void onStart() {
        refreshExplanationText();
        super.onStart();
    }

    private void refreshExplanationText(){
        // change txtExplanation based on current reward and walking distance
        TextView txtExplanation = findViewById(R.id.activity_reward_text_explanation);
        if (Reward.currentLevel == Reward.LEVEL_END){
            txtExplanation.setText("No more reward to collect, good job! More rewards will come tomorrow!");
            return;
        }
        Reward currentReward = Reward.getCurrentReward();
        Double targetDistance = Objects.requireNonNull(currentReward).getThresholdDistance();
        if (targetDistance > User.walkingDistance){
            txtExplanation.setText(String.format(Locale.UK, "Reach %1$.2f meters to get next reward!", targetDistance));
        } else {
            txtExplanation.setText(String.format(Locale.UK,"Reached %1$.2f meters, collect reward now!", targetDistance));
        }
    }
}
