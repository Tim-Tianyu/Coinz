package com.example.tim.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ReceiveGiftListAdapter extends RecyclerView.Adapter<ReceiveGiftListAdapter.MyViewHolder> {
    // adapter for list of gift receive
    private ArrayList<Gift> giftList;
    private LayoutInflater mInflater;
    private static boolean haveFocus;
    private static ReceiveGiftListAdapter currentAdapter;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtName;
        TextView txtValue;
        Button btnReceive;

        MyViewHolder(View v) {
            super(v);
            txtName = itemView.findViewById(R.id.receive_gift_list_txt_name);
            txtValue = itemView.findViewById(R.id.receive_gift_list_txt_value);
            btnReceive = itemView.findViewById(R.id.receive_gift_list_btn_receive);
        }
    }

    ReceiveGiftListAdapter(Context context, ArrayList<Gift> giftList){
        this.mInflater = LayoutInflater.from(context);
        this.giftList = giftList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.receive_gift_list_row, viewGroup,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Gift gift = giftList.get(i);
        User sender = User.findFriendById(gift.getSenderId());
        // gift from friend user have deleted will be show as gift from unknown friend
        if (sender != null) myViewHolder.txtName.setText(String.format(Locale.UK,"%s", sender.getName()));
        else myViewHolder.txtName.setText("Unknown friend");

        // show gold value for the gift
        myViewHolder.txtValue.setText(String.format(Locale.UK,"%1$.2f", gift.getValue()));
        myViewHolder.btnReceive.setOnClickListener(v -> {
            // receive gift
            int position = myViewHolder.getAdapterPosition();
            Bank.theBank.collectGift(ReceiveGiftListAdapter.this, giftList.get(position), position);
        });
    }

    void addItem(Gift gift){
        giftList.add(gift);
        notifyItemInserted(giftList.size()-1);
        notifyItemRangeChanged(giftList.size()-1, giftList.size());
    }

    void removeItem(int position){
        giftList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, giftList.size());
    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }

    // below record lifecycle for adapter, used to update adapter in real time from firebase listener
    static void onCurrentAdapterEnd(){
        haveFocus = false;
        currentAdapter = null;
    }

    static void onStartAdapter(ReceiveGiftListAdapter adapter){
        haveFocus = true;
        currentAdapter = adapter;
    }

    static ReceiveGiftListAdapter getCurrentAdapter(){
        if (!haveFocus) return  null;
        return currentAdapter;
    }
}
