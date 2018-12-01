package com.example.tim.coinz;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendSelectListAdapter extends RecyclerView.Adapter<FriendSelectListAdapter.MyViewHolder> {
    private ArrayList<User> friendList;
    private Coin giftCoin;
    private LayoutInflater mInflater;
    private Dialog dialog;
    private CoinListAdapter adapter;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public Button btnSelect;

        public MyViewHolder(View v) {
            super(v);
            txtName = itemView.findViewById(R.id.friend_list_txt_name);
            btnSelect = itemView.findViewById(R.id.friend_list_btn_delete);
        }
    }

    public FriendSelectListAdapter(Context context, CoinListAdapter adapter, Coin giftCoin, ArrayList<User> filteredFriendList) {
        this.mInflater = LayoutInflater.from(context);
        this.friendList = filteredFriendList;
        this.giftCoin = giftCoin;
        this.adapter = adapter;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public FriendSelectListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.friend_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        User friend = friendList.get(i);
        holder.txtName.setText(friend.getName());
        holder.btnSelect.setText("Select");
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                User friend = friendList.get(position);
                Coin.sendCoinAsGift(dialog, adapter, giftCoin, friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
