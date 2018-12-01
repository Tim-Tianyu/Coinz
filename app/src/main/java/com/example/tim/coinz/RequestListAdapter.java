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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtId;
        public Button btnReject;
        public Button btnAccept;

        public MyViewHolder(View v) {
            super(v);
            txtId = itemView.findViewById(R.id.request_list_txt_id);
            btnAccept = itemView.findViewById(R.id.request_list_btn_accept);
            btnReject = itemView.findViewById(R.id.request_list_btn_reject);
        }
    }

    public RequestListAdapter(Context context, ArrayList<Request> receivedRequestList){
        this.mInflater = LayoutInflater.from(context);
        this.receivedRequestList = receivedRequestList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.request_list_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Request request = receivedRequestList.get(i);
        myViewHolder.txtId.setText(request.getSenderId());
        myViewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = myViewHolder.getAdapterPosition();
                User.acceptFriendRequest(RequestListAdapter.this, request, position);
            }
        });
        myViewHolder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void removeItem(int position){
        receivedRequestList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, receivedRequestList.size());
    }

    @Override
    public int getItemCount() {
        return receivedRequestList.size();
    }
}
