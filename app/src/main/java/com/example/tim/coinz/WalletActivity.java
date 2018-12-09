package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class WalletActivity extends AppCompatActivity {
    // activity to show list of coin collected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        RecyclerView mRecyclerView = findViewById(R.id.CoinList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter mAdapter = new CoinListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Button btnReceive = findViewById(R.id.activity_wallet_btn_receive);
        btnReceive.setOnClickListener(v -> {
            // show dialog to show list of gift received form friends
            ReceiveGiftListAdapter adapter = new ReceiveGiftListAdapter(WalletActivity.this, Gift.receivedGifts);
            ListViewDialog dialog = new ListViewDialog(WalletActivity.this, adapter);
            ReceiveGiftListAdapter.onStartAdapter(adapter);
            dialog.show();
        });
    }
}
