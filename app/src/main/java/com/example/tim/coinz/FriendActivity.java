package com.example.tim.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

public class FriendActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AddFriendDialog addFriendDialog;
    private ListViewDialog listViewDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        addFriendDialog = new AddFriendDialog(FriendActivity.this);
        listViewDialog  = new ListViewDialog(FriendActivity.this, new RequestListAdapter(FriendActivity.this, (FriendListAdapter) mAdapter, Request.receivedRequests));
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_friend_rv_friends);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FriendListAdapter(this, User.friends);
        mRecyclerView.setAdapter(mAdapter);

        Button btnAdd = (Button) findViewById(R.id.activity_friend_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog.show();
            }
        });

        Button btnRequests = (Button) findViewById(R.id.activity_friend_btn_requests);
        btnRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewDialog.show();
            }
        });
    }
}
