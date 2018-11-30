package com.example.tim.coinz;

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
    private ArrayList<Gift> giftList;
    private ArrayList<User> friendList;
    private LayoutInflater mInflater;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public TextView txtValue;
        public Button btnReceive;

        public MyViewHolder(View v) {
            super(v);
            txtName = itemView.findViewById(R.id.receive_gift_list_txt_name);
            txtValue = itemView.findViewById(R.id.receive_gift_list_txt_value);
            btnReceive = itemView.findViewById(R.id.receive_gift_list_btn_receive);
        }
    }

    public ReceiveGiftListAdapter(Context context, ArrayList<Gift> giftList, ArrayList<User> friendList){
        this.mInflater = LayoutInflater.from(context);
        this.giftList = giftList;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.receive_gift_list_row, viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Gift gift = giftList.get(i);
        User sender = User.findUserById(friendList,gift.getSenderId());
        if (sender != null) myViewHolder.txtName.setText(String.format(Locale.UK,"%s", sender.getName()));
        else myViewHolder.txtName.setText("Unknown friend");

        myViewHolder.txtValue.setText(String.format(Locale.UK,"%1$.2f", gift.getValue()));
        myViewHolder.btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();
                Bank.theBank.receiveGift(ReceiveGiftListAdapter.this, giftList.get(position), position);
            }
        });
    }

    public void removeItem(int position){
        giftList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, giftList.size());
    }

    @Override
    public int getItemCount() {
        return giftList.size();
    }
}
