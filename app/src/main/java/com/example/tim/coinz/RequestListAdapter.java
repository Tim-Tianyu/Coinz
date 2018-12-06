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

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{
    private ArrayList<Request> receivedRequestList;
    private LayoutInflater mInflater;
    private FriendListAdapter friendListAdapter;
    private static boolean haveFocus;
    private static RequestListAdapter currentAdapter;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView txtId;
        Button btnReject;
        Button btnAccept;

        MyViewHolder(View v) {
            super(v);
            txtId = itemView.findViewById(R.id.request_list_txt_id);
            btnAccept = itemView.findViewById(R.id.request_list_btn_accept);
            btnReject = itemView.findViewById(R.id.request_list_btn_reject);
        }
    }

    RequestListAdapter(Context context, FriendListAdapter friendListAdapter, ArrayList<Request> receivedRequestList){
        this.mInflater = LayoutInflater.from(context);
        this.receivedRequestList = receivedRequestList;
        this.friendListAdapter = friendListAdapter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.request_list_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Request request = receivedRequestList.get(i);
        myViewHolder.txtId.setText(request.getSenderId());
        myViewHolder.btnAccept.setOnClickListener(v -> {
            int position = myViewHolder.getAdapterPosition();
            User.acceptFriendRequest(RequestListAdapter.this, friendListAdapter, request, position);
        });
        myViewHolder.btnReject.setOnClickListener(v -> {
            int position = myViewHolder.getAdapterPosition();
            User.rejectFriendRequest(RequestListAdapter.this, request, position);
        });
    }

    void removeItem(int position){
        receivedRequestList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, receivedRequestList.size());
    }

    void addItem(Request request){
        receivedRequestList.add(request);
        notifyItemInserted(receivedRequestList.size()-1);
        notifyItemRangeChanged(receivedRequestList.size()-1, receivedRequestList.size());
    }

    @Override
    public int getItemCount() {
        return receivedRequestList.size();
    }

    static void onCurrentAdapterEnd(){
        haveFocus = false;
        currentAdapter = null;
    }

    static void onStartAdapter(RequestListAdapter adapter){
        haveFocus = true;
        currentAdapter = adapter;
    }

    static RequestListAdapter getCurrentAdapter(){
        if (!haveFocus) return  null;
        return currentAdapter;
    }
}
