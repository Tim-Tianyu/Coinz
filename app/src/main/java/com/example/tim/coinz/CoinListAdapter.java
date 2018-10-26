package com.example.tim.coinz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CoinListAdapter extends RecyclerView.Adapter<CoinListAdapter.MyViewHolder>{
    private ArrayList<Coin> coinList;
    private LayoutInflater mInflater;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public MyViewHolder(View v) {
            super(v);
            mTextView = itemView.findViewById(R.id.message);
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
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Coin coin = coinList.get(position);
        String s = String.format("%s: %f", coin.getCurrency().toString(),  coin.getValue());
        holder.mTextView.setText(s);
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }
}
