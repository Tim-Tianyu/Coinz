package com.example.tim.coinz;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class FirebaseListener {
    static FirebaseListener data = new FirebaseListener();
    private static String TAG = "DATABASE";
    private static Boolean isUserDownload = false;
    private static Boolean isFriendsDownload = false;
    private static Boolean isSentGiftsDownload = false;
    private static Boolean isReceivedGiftsDownload = false;
    private static Boolean isSentRequestsDownload = false;
    private static Boolean isReceivedRequestsDownload = false;
    static ArrayList<Gift> sentGifts = new ArrayList<>();
    static ArrayList<Gift> receivedGifts = new ArrayList<>();
    static User currentUser;
    static ArrayList<User> friends = new ArrayList<>();
    static ArrayList<Request> sentRequests = new ArrayList<>();
    static ArrayList<Request> receivedRequests = new ArrayList<>();
    static Set<String> friendsId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void downloadData(String userId){
        isUserDownload = false;
        isFriendsDownload = false;
        isSentGiftsDownload = false;
        isReceivedGiftsDownload = false;
        isSentRequestsDownload = false;
        isReceivedRequestsDownload = false;
        downloadUser(userId);
        downloadFriends(userId);
        downloadSentGifts(userId);
        downloadReceivedGifts(userId);
        downloadSentRequests(userId);
        downloadReceivedRequests(userId);

    }

    private void downloadUser(String userId){
        db.collection("USER").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    try {
                        currentUser = new User(result.getId(), result.getDouble("Gold"), result.getString("Name"));
                        isUserDownload = true;
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


    private void downloadFriends(String userId) {
        db.collection("USER").whereEqualTo(String.format("FriendList.List.%s", userId), true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            friends.add(new User(document.getId(), document.getDouble("Gold"), document.getString("Name")));
                        }
                        isFriendsDownload = true;
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadSentGifts(String userId) {
        db.collection("GIFT").whereEqualTo("Sender", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            sentGifts.add(new Gift(document.getId(), document.getDouble("Value"), document.getBoolean("IsReceived"), document.getDocumentReference("Sender").getId(), document.getDocumentReference("Receiver").getId()));
                        }
                        isSentGiftsDownload = true;
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadReceivedGifts(String userId) {
        db.collection("GIFT").whereEqualTo("Receiver", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            receivedGifts.add(new Gift(document.getId(), document.getDouble("Value"), document.getBoolean("IsReceived"), document.getDocumentReference("Sender").getId(), document.getDocumentReference("Receiver").getId()));
                        }
                        isReceivedGiftsDownload = true;
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadSentRequests(String userId) {
        db.collection("GIFT").whereEqualTo("Sender", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            sentRequests.add(new Request(document.getId(), document.getString("Sender"), document.getString("Receiver"), Request.DoubleToStatus(document.getDouble("Status"))));
                        }
                        isSentRequestsDownload = true;
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadReceivedRequests(String userId) {
        db.collection("GIFT").whereEqualTo("Sender", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            receivedRequests.add(new Request(document.getId(), document.getString("Sender"), document.getString("Receiver"), Request.DoubleToStatus(document.getDouble("Status"))));
                        }
                        isReceivedRequestsDownload = true;
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadStateChecking() {
        if (isUserDownload && isFriendsDownload && isSentGiftsDownload && isReceivedGiftsDownload && isSentRequestsDownload && isReceivedRequestsDownload){
            //TODO
        }
    }
}
