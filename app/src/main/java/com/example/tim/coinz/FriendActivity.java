package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class FriendActivity extends AppCompatActivity {
    // activity to show friend list

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        mRecyclerView =  findViewById(R.id.activity_friend_rv_friends);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Button btnAdd = findViewById(R.id.activity_friend_btn_add);
        btnAdd.setOnClickListener(v -> {
            // user want to add friend
            AddFriendDialog addFriendDialog = new AddFriendDialog(FriendActivity.this);
            addFriendDialog.show();
        });

        Button btnRequests = findViewById(R.id.activity_friend_btn_requests);
        btnRequests.setOnClickListener(v -> {
            // user want to view received request
            RequestListAdapter adapter = new RequestListAdapter(FriendActivity.this, (FriendListAdapter) mAdapter, Request.receivedRequests);
            ListViewDialog listViewDialog  = new ListViewDialog(FriendActivity.this, adapter);
            listViewDialog.show();
            RequestListAdapter.onStartAdapter(adapter);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new FriendListAdapter(this, User.friends);
        mRecyclerView.setAdapter(mAdapter);
        FriendListAdapter.onStartAdapter((FriendListAdapter)mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FriendListAdapter.onCurrentAdapterEnd();
    }
}
