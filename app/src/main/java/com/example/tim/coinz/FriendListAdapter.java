package com.example.tim.coinz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder>{
    private ArrayList<User> friendList;
    private LayoutInflater mInflater;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public Button btnDelete;

        public MyViewHolder(View v) {
            super(v);
            txtName = itemView.findViewById(R.id.friend_list_txt_name);
            btnDelete = itemView.findViewById(R.id.friend_list_btn_delete);
        }
    }

    public FriendListAdapter(Context context, ArrayList<User> friendList) {
        this.mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
    }

    @Override
    public FriendListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.friend_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        User friend = friendList.get(i);
        holder.txtName.setText(friend.getName());
        // TODO holder.btnDelete
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


}
