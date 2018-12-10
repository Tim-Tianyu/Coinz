package com.example.tim.coinz;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Request {
    // represent friend requests received or send
    enum Status {
        PENDING,
        ACCEPT,
        DENY
    }

    private String requestId;
    private String senderId;
    private String receiverId;
    // not used now, will be useful sometime
    private Status status;
    private Timestamp timestamp;

    // listeners for firestore
    private static ListenerRegistration requestStateListener;
    private static ListenerRegistration receiveRequestListener;

    static final Double PENDING = 0.0;
    static final Double ACCEPT = 1.0;
    static final Double DENY = 2.0;
    private static final String TAG = "Request";

    // static arrayList hold the requests
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
        // add listener to listen for any sent request being accepted or deny
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
                                // update friend list if request is accept
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
        // add listener to listen for any new request being received
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
                            Request newRequest = new Request(requestId, Objects.requireNonNull(snapshot.getDocumentReference("Sender")).getId(), Objects.requireNonNull(snapshot.getDocumentReference("Receiver")).getId(), Request.DoubleToStatus(Objects.requireNonNull(snapshot.getDouble("Status"))), snapshot.getTimestamp("Time"));

                            // adapter will be null if it don't have focus currently
                            RequestListAdapter adapter = RequestListAdapter.getCurrentAdapter();
                            if (adapter != null) {
                                // update adapter as it has focus
                                adapter.addItem(newRequest);
                            } else {
                                receivedRequests.add(newRequest);
                            }
                        }
                    }
                });

    }

    static void sendNewRequest(Context context, String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        CollectionReference collectionReference = db.collection("USER");
        DocumentReference documentReference = collectionReference.document(User.currentUser.getUserId());
        documentReference.getPath();

        Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime());
        data.put("Status", Request.PENDING);
        data.put("Time", timestamp);
        data.put("Sender", collectionReference.document(User.currentUser.getUserId()));
        data.put("Receiver", collectionReference.document(userId));
        db.collection("FRIEND_REQUEST").add(data)
                .addOnSuccessListener(documentReference1 -> {
                    Request.sentRequests.add(new Request(documentReference1.getId(), User.currentUser.getUserId(), userId, Request.Status.PENDING, timestamp));
                    Toast.makeText(context, "Request successfully sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, e);
                    Toast.makeText(context, "Request fail to sent", Toast.LENGTH_SHORT).show();
                });
    }

    static void detachAllListener(){
        if (requestStateListener != null) requestStateListener.remove();
        if (receiveRequestListener != null) receiveRequestListener.remove();
    }
}
