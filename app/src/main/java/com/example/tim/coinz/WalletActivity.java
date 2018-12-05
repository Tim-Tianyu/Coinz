package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class WalletActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        mRecyclerView = (RecyclerView) findViewById(R.id.CoinList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CoinListAdapter(this, Coin.collectedCoinsList);
        mRecyclerView.setAdapter(mAdapter);

        Button btnReceive = (Button) findViewById(R.id.activity_wallet_btn_receive);
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveGiftListAdapter adapter = new ReceiveGiftListAdapter(WalletActivity.this, Gift.receivedGifts);
                ListViewDialog dialog = new ListViewDialog(WalletActivity.this, adapter);
                ReceiveGiftListAdapter.onStartAdapter(adapter);
                dialog.show();
            }
        });
    }
}
