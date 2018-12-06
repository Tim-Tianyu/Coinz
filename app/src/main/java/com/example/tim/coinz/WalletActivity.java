package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        RecyclerView mRecyclerView = findViewById(R.id.CoinList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new CoinListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Button btnReceive = findViewById(R.id.activity_wallet_btn_receive);
        btnReceive.setOnClickListener(v -> {
            ReceiveGiftListAdapter adapter = new ReceiveGiftListAdapter(WalletActivity.this, Gift.receivedGifts);
            ListViewDialog dialog = new ListViewDialog(WalletActivity.this, adapter);
            ReceiveGiftListAdapter.onStartAdapter(adapter);
            dialog.show();
        });
    }
}
