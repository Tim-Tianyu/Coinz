package com.example.tim.coinz;


import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

class Request {
    enum Status {
        PENDING,
        ACCEPT,
        DENY
    }

    private String requestId;
    private String senderId;
    private String receiverId;
    private Status status;
    private Timestamp timestamp;
    private static ListenerRegistration requestStateListener;
    private static ListenerRegistration receiveRequestListener;

    static final Double PENDING = 0.0;
    static final Double ACCEPT = 1.0;
    static final Double DENY = 2.0;
    private static final String TAG = "Request";
    static ArrayList<Request> sentRequests = new ArrayList<>();
    static ArrayList<Request> receivedRequests = new ArrayList<>();

    Request(String requestId, String senderId, String receiverId, Status status, Timestamp timestamp) {
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.status = status;
        this.timestamp = timestamp;
    }

    String getRequestId() {
        return requestId;
    }

    String getSenderId() {
        return senderId;
    }

    static Status DoubleToStatus(Double num) {
        if (num.equals(PENDING)) return Status.PENDING;
        else if (num.equals(ACCEPT)) return Status.ACCEPT;
        else if (num.equals(DENY)) return Status.PENDING;
        else return null;
    }

    private static Request findRequestById(ArrayList<Request> requestList, String requestId) {
        for (Request request : requestList){
            if (requestId.equals(request.requestId)){
                return request;
            }
        }
        return null;
    }

    static Request findReceivedRequestBySenderId(String senderId) {
        for (Request request : receivedRequests){
            if (senderId.equals(request.senderId)){
                return request;
            }
        }
        return null;
    }

    static Request findSentRequestByReceiverId(String receiverId){
        for (Request request : sentRequests){
            if (receiverId.equals(request.receiverId)){
                return request;
            }
        }
        return null;
    }

    static void addRequestStateChangeListener() {
        if (requestStateListener != null) requestStateListener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        requestStateListener = db.collection("FRIEND_REQUEST").whereEqualTo("Sender", db.collection("USER").document(User.currentUser.getUserId()))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "RequestStateChangeListener:error", e);
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()){
                        if (dc.getType().equals(DocumentChange.Type.MODIFIED)){
                            QueryDocumentSnapshot snapshot = dc.getDocument();
                            Double status = snapshot.getDouble("Status");
                            if (status==null) return;
                            if (status.equals(Request.ACCEPT)){
                                Request request = findRequestById(sentRequests, snapshot.getId());
                                if (request == null){
                                    Log.w(TAG, "accepted sent request not found");
                                    return;
                                }
                                sentRequests.remove(request);
                                User.updateFriendListOnRequestAccept(request.receiverId);
                            } else if (status.equals(Request.DENY)) {
                                Request request = findRequestById(sentRequests, snapshot.getId());
                                if (request == null){
                                    Log.w(TAG, "denied sent request not found");
                                    return;
                                }
                                sentRequests.remove(request);
                            }
                        }
                    }
                });
    }

    static void addReceiveRequestListener(){
        if (receiveRequestListener != null) receiveRequestListener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        receiveRequestListener = db.collection("FRIEND_REQUEST").whereEqualTo("Receiver", db.collection("USER").document(User.currentUser.getUserId())).whereEqualTo("Status", PENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "ReceiveRequestListener:error", e);
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()){
                        if (dc.getType().equals(DocumentChange.Type.ADDED)){
                            QueryDocumentSnapshot snapshot = dc.getDocument();
                            String requestId = snapshot.getId();
                            if (findRequestById(receivedRequests, requestId) != null) return;
                            RequestListAdapter adapter = RequestListAdapter.getCurrentAdapter();
                            Request newRequest;
                            try{
                                newRequest = new Request(requestId, Objects.requireNonNull(snapshot.getDocumentReference("Sender")).getId(), Objects.requireNonNull(snapshot.getDocumentReference("Receiver")).getId(), Request.DoubleToStatus(Objects.requireNonNull(snapshot.getDouble("Status"))), snapshot.getTimestamp("Time"));
                            } catch (NullPointerException ex) {
                                Log.w(TAG, ex);
                                return;
                            }
                            if (adapter != null) {
                                adapter.addItem(newRequest);
                            } else {
                                receivedRequests.add(newRequest);
                            }
                        }
                    }
                });

    }

    static void detachAllListener(){
        if (requestStateListener != null) requestStateListener.remove();
        if (receiveRequestListener != null) receiveRequestListener.remove();
    }
}
