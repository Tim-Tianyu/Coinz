package com.example.tim.coinz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CoinListAdapter extends RecyclerView.Adapter<CoinListAdapter.MyViewHolder>{
    private ArrayList<Coin> coinList;
    private LayoutInflater mInflater;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public Button btnBank;
        public Button btnGift;

        public MyViewHolder(View v) {
            super(v);
            mTextView = itemView.findViewById(R.id.message);
            mImageView = itemView.findViewById(R.id.coin_icon);
            btnBank = itemView.findViewById(R.id.btnBank);
            btnGift = itemView.findViewById(R.id.btnGift);
        }
    }

    public CoinListAdapter(Context context, ArrayList<Coin> coinList) {
        this.mInflater = LayoutInflater.from(context);
        this.coinList = coinList;
    }

    @Override
    public CoinListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = mInflater.inflate(R.layout.coin_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Coin coin = coinList.get(position);
        String s = String.format("%s: %f", coin.getCurrency().toString(),  coin.getValue());
        holder.mTextView.setText(s);
        switch (coin.getCurrency()){
            case DOLR: holder.mImageView.setImageResource(R.drawable.ic_baseline_room_dolr);
                break;
            case QUID: holder.mImageView.setImageResource(R.drawable.ic_baseline_room_quid);
                break;
            case PENNY: holder.mImageView.setImageResource(R.drawable.ic_baseline_room_penny);
                break;
            case SHIL: holder.mImageView.setImageResource(R.drawable.ic_baseline_room_shil);
                break;
        }
        holder.btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coinList.remove(position);
                //setEmptyViewVisibility();
                notifyItemRemoved(position);
                Bank.theBank.saveCoin(coin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }
}
