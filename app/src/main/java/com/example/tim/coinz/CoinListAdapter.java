package com.example.tim.coinz;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CoinListAdapter extends RecyclerView.Adapter<CoinListAdapter.MyViewHolder>{
    private ArrayList<Coin> coinList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public MyViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public CoinListAdapter(ArrayList<Coin> coinList) {
        coinList = coinList;
    }

    @Override
    public CoinListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin_list_row, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        coinList.get(position)
        holder.mTextView.setText(coinList.get(position).getCurrency().toString() + );
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }
}
