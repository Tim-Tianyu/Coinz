package com.example.tim.coinz;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class ListViewDialog extends Dialog {
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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        Button btnClose = findViewById(R.id.list_view_dialog_btn_close);
        btnClose.setOnClickListener(v -> {
            if (RequestListAdapter.class.isInstance(mAdapter)) RequestListAdapter.onCurrentAdapterEnd();
            if (FriendSelectListAdapter.class.isInstance(mAdapter)) FriendSelectListAdapter.onCurrentAdapterEnd();
            if (ReceiveGiftListAdapter.class.isInstance(mAdapter)) ReceiveGiftListAdapter.onCurrentAdapterEnd();
            dismiss();
        });
    }
}
