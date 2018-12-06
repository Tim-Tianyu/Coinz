package com.example.tim.coinz;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder>{
    private ArrayList<User> friendList;
    private LayoutInflater mInflater;
    private AlertDialog dialog;
    private int currentPosition = 0;
    private static boolean haveFocus;
    private static FriendListAdapter currentAdapter;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtName;
        Button btnDelete;

        MyViewHolder(View v) {
            super(v);
            txtName = itemView.findViewById(R.id.friend_list_txt_name);
            btnDelete = itemView.findViewById(R.id.friend_list_btn_delete);
        }
    }

    FriendListAdapter(Context context, ArrayList<User> friendList) {
        this.mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        dialog = builder.setTitle("Delete Friend")
                .setMessage("Are you sure you want to delete this friend?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> User.deleteFriend(friendList.get(currentPosition)))
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();
    }

    @NonNull
    @Override
    public FriendListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.friend_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        User friend = friendList.get(i);
        holder.txtName.setText(friend.getName());
        holder.btnDelete.setOnClickListener(v -> {
            currentPosition = holder.getAdapterPosition();
            dialog.show();
            // TODO need refactor all the adapters to remove redundant parameters like "friendList.get(position)"
        });
    }

    void addItem(User friend){
        friendList.add(friend);
        notifyItemInserted(friendList.size()-1);
        notifyItemRangeChanged(friendList.size()-1, friendList.size());
    }

    void removeItem(int position) {
        friendList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, friendList.size());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static void onCurrentAdapterEnd(){
        haveFocus = false;
        currentAdapter = null;
    }

    static void onStartAdapter(FriendListAdapter adapter){
        haveFocus = true;
        currentAdapter = adapter;
    }

    static FriendListAdapter getCurrentAdapter(){
        if (!haveFocus) return  null;
        return currentAdapter;
    }
}
