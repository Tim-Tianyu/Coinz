package com.example.tim.coinz;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class FirebaseListener {
    //static FirebaseListener data = new FirebaseListener();
    private static final String TAG = "FirebaseListener";
    private static Boolean isUserDownload = false;
    private static Boolean isFriendsDownload = false;
    private static Boolean isSentGiftsDownload = false;
    private static Boolean isReceivedGiftsDownload = false;
    private static Boolean isSentRequestsDownload = false;
    private static Boolean isReceivedRequestsDownload = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void downloadData(LoadActivity activity, String userId){
        clearCurrentFirestoreData();
        isUserDownload = false;
        isFriendsDownload = false;
        isSentGiftsDownload = false;
        isReceivedGiftsDownload = false;
        isSentRequestsDownload = false;
        isReceivedRequestsDownload = false;
        downloadUser(activity, userId);
        downloadFriends(activity, userId);
        downloadSentGifts(activity, userId);
        downloadReceivedGifts(activity, userId);
        downloadSentRequests(activity, userId);
        downloadReceivedRequests(activity, userId);
    }

    private void downloadUser(LoadActivity activity, String userId){
        db.collection("USER").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    try {
                        User.currentUser = new User(result.getId(), result.getString("Name"));
                        isUserDownload = true;
                        Log.i(TAG, "currentUser download complete");
                        downloadStateChecking(activity);
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


    private void downloadFriends(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        db.collection("USER").whereArrayContains("FriendList", userRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            User.friends.add(new User(document.getId(), document.getString("Name")));
                        }
                        isFriendsDownload = true;
                        Log.i(TAG, "friends download complete");
                        downloadStateChecking(activity);
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadSentGifts(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        db.collection("GIFT").whereEqualTo("Sender", userRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Gift.sentGifts.add(new Gift(document.getId(), document.getDouble("Value"), document.getBoolean("IsReceived"), document.getDocumentReference("Sender").getId(), document.getDocumentReference("Receiver").getId(), document.getTimestamp("Time")));
                        }
                        isSentGiftsDownload = true;
                        Log.i(TAG, "sentGifts download complete");
                        downloadStateChecking(activity);
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadReceivedGifts(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        // only need unreceived gift to receive gift
        db.collection("GIFT").whereEqualTo("Receiver", userRef).whereEqualTo("IsReceived", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Gift.receivedGifts.add(new Gift(document.getId(), document.getDouble("Value"), document.getBoolean("IsReceived"), document.getDocumentReference("Sender").getId(), document.getDocumentReference("Receiver").getId(), document.getTimestamp("Time")));
                        }
                        isReceivedGiftsDownload = true;
                        Log.i(TAG, "receivedGifts download complete");
                        downloadStateChecking(activity);
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadSentRequests(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        db.collection("FRIEND_REQUEST").whereEqualTo("Sender", userRef).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            document.getDouble("Status");
                            Request.sentRequests.add(new Request(document.getId(), document.getDocumentReference("Sender").getId(), document.getDocumentReference("Receiver").getId(), Request.DoubleToStatus(document.getDouble("Status")), document.getTimestamp("Time")));
                        }
                        isSentRequestsDownload = true;
                        Log.i(TAG, "sentRequests download complete");
                        downloadStateChecking(activity);
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadReceivedRequests(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        // only need pending request for received request
        db.collection("FRIEND_REQUEST").whereEqualTo("Receiver", userRef).whereEqualTo("Status", Request.PENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    try {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Request.receivedRequests.add(new Request(document.getId(), document.getDocumentReference("Sender").getId(), document.getDocumentReference("Receiver").getId(), Request.DoubleToStatus(document.getDouble("Status")), document.getTimestamp("Time")));
                        }
                        isReceivedRequestsDownload = true;
                        Log.i(TAG, "receivedRequests download complete");
                        downloadStateChecking(activity);
                    } catch (NullPointerException ex){
                        Log.w(TAG, "Null pointer when retriving result.", ex);
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void downloadStateChecking(LoadActivity activity) {
        if (isUserDownload && isFriendsDownload && isSentGiftsDownload && isReceivedGiftsDownload && isSentRequestsDownload && isReceivedRequestsDownload){
            Request.addRequestStateChangeListener();
            Request.addReceiveRequestListener();
            User.addFriendDeleteListener();
            Gift.addReceiveGiftListener();
            Gift.addSentGiftReceivedListener();
            activity.onCompleteDownloadFirebaseData();
        }
    }

    private void downloadFail(LoadActivity activity) {
        //TODO
    }

    static void clearCurrentFirestoreData(){
        Coin.coinsList = new ArrayList<>();
        Gift.sentGifts = new ArrayList<>();
        Gift.receivedGifts = new ArrayList<>();
        Request.sentRequests = new ArrayList<>();
        Request.receivedRequests = new ArrayList<>();
        User.friends = new ArrayList<>();
        User.currentUser = null;
    }
}
