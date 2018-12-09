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
    // adapter for list of friends to select when user want to send gift
    private ArrayList<User> friendList;
    private Coin giftCoin;
    private LayoutInflater mInflater;
    private Dialog dialog;
    private CoinListAdapter adapter;
    private static boolean haveFoucus;
    private static FriendSelectListAdapter currentAdapter;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        Button btnSelect;

        MyViewHolder(View v) {
            super(v);
            txtName = itemView.findViewById(R.id.friend_list_txt_name);
            btnSelect = itemView.findViewById(R.id.friend_list_btn_delete);
        }
    }

    FriendSelectListAdapter(Context context, CoinListAdapter adapter, Coin giftCoin, ArrayList<User> filteredFriendList) {
        this.mInflater = LayoutInflater.from(context);
        this.friendList = filteredFriendList;
        this.giftCoin = giftCoin;
        this.adapter = adapter;
    }

    void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @NonNull
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
        holder.btnSelect.setOnClickListener(v -> {
            // select friend to send gift
            int position = holder.getAdapterPosition();
            User friend1 = friendList.get(position);
            Coin.sendCoinAsGift(dialog, adapter, giftCoin, friend1);
        });
    }

    void removeItemById(String userId){
        for (User user : friendList){
            if (userId.equals(user.getUserId())){
                int position = friendList.indexOf(user);
                friendList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, friendList.size());
            }
        }
    }

    void addItem(User user){
        friendList.add(user);
        notifyItemInserted(friendList.size()-1);
        notifyItemRangeChanged(friendList.size()-1, friendList.size());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    // below record lifecycle for adapter, used to update adapter in real time from firebase listener
    static void onCurrentAdapterEnd(){
        haveFoucus = false;
        currentAdapter = null;
    }

    static void onStartAdapter(FriendSelectListAdapter adapter){
        haveFoucus = true;
        currentAdapter = adapter;
    }

    static FriendSelectListAdapter getCurrentAdapter(){
        if (!haveFoucus) return  null;
        return currentAdapter;
    }
}
