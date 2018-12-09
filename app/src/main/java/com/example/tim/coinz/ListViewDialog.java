package com.example.tim.coinz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class ListViewDialog extends Dialog {
    // list view dialog is used multiple times, it show a list of thing and close button
    private RecyclerView.Adapter mAdapter;
    private Context context;

    ListViewDialog(Context context, RecyclerView.Adapter adapter) {
        super(context);
        this.context = context;
        this.mAdapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_dialog);
        RecyclerView mRecyclerView = findViewById(R.id.list_view_dialog_rv_list);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        Button btnClose = findViewById(R.id.list_view_dialog_btn_close);
        btnClose.setOnClickListener(v -> {
            // three kind of adapters will be used
            // update adapter lifecycle
            if (RequestListAdapter.class.isInstance(mAdapter)) RequestListAdapter.onCurrentAdapterEnd();
            if (FriendSelectListAdapter.class.isInstance(mAdapter)) FriendSelectListAdapter.onCurrentAdapterEnd();
            if (ReceiveGiftListAdapter.class.isInstance(mAdapter)) ReceiveGiftListAdapter.onCurrentAdapterEnd();
            dismiss();
        });
    }
}
