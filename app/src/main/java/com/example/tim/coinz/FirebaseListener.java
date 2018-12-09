package com.example.tim.coinz;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class FirebaseListener {
    private static final String TAG = "FirebaseListener";
    private static Boolean isUserDownload = false;
    private static Boolean isFriendsDownload = false;
    private static Boolean isSentGiftsDownload = false;
    private static Boolean isReceivedGiftsDownload = false;
    private static Boolean isSentRequestsDownload = false;
    private static Boolean isReceivedRequestsDownload = false;
    private static Boolean isUserDownloadTaskFinished = false;
    private static Boolean isFriendsDownloadTaskFinished = false;
    private static Boolean isSentGiftsDownloadTaskFinished = false;
    private static Boolean isReceivedGiftsDownloadTaskFinished = false;
    private static Boolean isSentRequestsDownloadTaskFinished = false;
    private static Boolean isReceivedRequestsDownloadTaskFinished = false;
    private static Boolean isErrorReported = false;
    private static Exception reportedException;
    private static String userIdTemp;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    void downloadData(LoadActivity activity, String userId){
        clearCurrentFirestoreData();
        isUserDownload = false;
        isFriendsDownload = false;
        isSentGiftsDownload = false;
        isReceivedGiftsDownload = false;
        isSentRequestsDownload = false;
        isReceivedRequestsDownload = false;
        isUserDownloadTaskFinished = false;
        isFriendsDownloadTaskFinished = false;
        isSentGiftsDownloadTaskFinished = false;
        isReceivedGiftsDownloadTaskFinished = false;
        isSentRequestsDownloadTaskFinished = false;
        isReceivedRequestsDownloadTaskFinished = false;
        isErrorReported = false;
        userIdTemp = userId;
        userId = "dwqrqwer";
        downloadUser(activity, userId);
        downloadFriends(activity, userId);
        downloadSentGifts(activity, userId);
        downloadReceivedGifts(activity, userId);
        downloadSentRequests(activity, userId);
        downloadReceivedRequests(activity, userId);
    }

    private void downloadUser(LoadActivity activity, String userId){
        db.collection("USER").document(userId).get().addOnCompleteListener(task -> {
            Log.i(TAG, "currentUser task complete");
            isUserDownloadTaskFinished = true;
            if (isErrorReported){
                downloadStateCheckingWhenFailing(activity);
                return;
            }
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                User.currentUser = new User(Objects.requireNonNull(result).getId(), result.getString("Name"));
                isUserDownload = true;
                Log.i(TAG, "currentUser download complete");
                downloadStateChecking(activity);
            } else {
                downloadFail(activity, task.getException());
            }
        });
    }

    private void downloadFriends(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        db.collection("USER").whereArrayContains("FriendList", userRef).get().addOnCompleteListener(task -> {
            Log.i(TAG, "friends task complete");
            isFriendsDownloadTaskFinished = true;
            if (isErrorReported){
                downloadStateCheckingWhenFailing(activity);
                return;
            }
            if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        User.friends.add(new User(document.getId(), document.getString("Name")));
                    }
                    isFriendsDownload = true;
                    Log.i(TAG, "friends download complete");
                    downloadStateChecking(activity);
            } else {
                downloadFail(activity, task.getException());
            }
        });
    }

    private void downloadSentGifts(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        db.collection("GIFT").whereEqualTo("Sender", userRef).get().addOnCompleteListener(task -> {
            Log.i(TAG, "sentGifts task complete");
            isSentGiftsDownloadTaskFinished = true;
            if (isErrorReported){
                downloadStateCheckingWhenFailing(activity);
                return;
            }
            if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Gift.sentGifts.add(new Gift(document.getId(), document.getDouble("Value"), document.getBoolean("IsReceived"), Objects.requireNonNull(document.getDocumentReference("Sender")).getId(), Objects.requireNonNull(document.getDocumentReference("Receiver")).getId(), document.getTimestamp("Time")));
                    }
                    isSentGiftsDownload = true;
                    Log.i(TAG, "sentGifts download complete");
                    downloadStateChecking(activity);
            } else {
                downloadFail(activity, task.getException());
            }
        });
    }

    private void downloadReceivedGifts(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        // only need unreceived gift to receive gift
        db.collection("GIFT").whereEqualTo("Receiver", userRef).whereEqualTo("IsReceived", false).get().addOnCompleteListener(task -> {
            Log.i(TAG, "receivedGifts task complete");
            isReceivedGiftsDownloadTaskFinished = true;
            if (isErrorReported){
                downloadStateCheckingWhenFailing(activity);
                return;
            }
            if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Gift.receivedGifts.add(new Gift(document.getId(), document.getDouble("Value"), document.getBoolean("IsReceived"), Objects.requireNonNull(document.getDocumentReference("Sender")).getId(), Objects.requireNonNull(document.getDocumentReference("Receiver")).getId(), document.getTimestamp("Time")));
                    }
                    isReceivedGiftsDownload = true;
                    Log.i(TAG, "receivedGifts download complete");
                    downloadStateChecking(activity);
            } else {
                downloadFail(activity, task.getException());
            }
        });
    }

    private void downloadSentRequests(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        db.collection("FRIEND_REQUEST").whereEqualTo("Sender", userRef).get().addOnCompleteListener(task -> {
            Log.i(TAG, "sentRequests task complete");
            isSentRequestsDownloadTaskFinished = true;
            if (isErrorReported){
                downloadStateCheckingWhenFailing(activity);
                return;
            }
            if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        document.getDouble("Status");
                        Request.sentRequests.add(new Request(document.getId(), Objects.requireNonNull(document.getDocumentReference("Sender")).getId(), Objects.requireNonNull(document.getDocumentReference("Receiver")).getId(), Request.DoubleToStatus(Objects.requireNonNull(document.getDouble("Status"))), document.getTimestamp("Time")));
                    }
                    isSentRequestsDownload = true;
                    Log.i(TAG, "sentRequests download complete");
                    downloadStateChecking(activity);
            } else {
                downloadFail(activity, task.getException());
            }
        });
    }

    private void downloadReceivedRequests(LoadActivity activity, String userId) {
        DocumentReference userRef = db.collection("USER").document(userId);
        // only need pending request for received request
        db.collection("FRIEND_REQUEST").whereEqualTo("Receiver", userRef).whereEqualTo("Status", Request.PENDING).get().addOnCompleteListener(task -> {
            Log.i(TAG, "receivedRequests task complete");
            isReceivedRequestsDownloadTaskFinished = true;
            if (isErrorReported){
                downloadStateCheckingWhenFailing(activity);
                return;
            }
            if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Request.receivedRequests.add(new Request(document.getId(), Objects.requireNonNull(document.getDocumentReference("Sender")).getId(), Objects.requireNonNull(document.getDocumentReference("Receiver")).getId(), Request.DoubleToStatus(Objects.requireNonNull(document.getDouble("Status"))), document.getTimestamp("Time")));
                    }
                    isReceivedRequestsDownload = true;
                    Log.i(TAG, "receivedRequests download complete");
                    downloadStateChecking(activity);
            } else {
                downloadFail(activity, task.getException());
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

    private void downloadFail(LoadActivity activity, Exception ex) {
        Log.w(TAG, "Error getting documents.", ex);
        reportedException = ex;
        isErrorReported = true;
        downloadStateCheckingWhenFailing(activity);
    }

    private void downloadStateCheckingWhenFailing(LoadActivity activity){
        if (isUserDownloadTaskFinished && isFriendsDownloadTaskFinished && isSentGiftsDownloadTaskFinished &&
                isReceivedGiftsDownloadTaskFinished && isSentRequestsDownloadTaskFinished && isReceivedRequestsDownloadTaskFinished) {
            if (FirebaseNetworkException.class.isInstance(reportedException) || FirebaseFirestoreException.class.isInstance(reportedException)){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Network Issue")
                        .setMessage("Experiencing network issue right now, try to reconnect?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            downloadData(activity, userIdTemp);
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) ->{
                            dialog.dismiss();
                            activity.finish();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Unknown Exception")
                        .setMessage(reportedException.getMessage())
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            downloadData(activity, userIdTemp);
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) ->{
                            dialog.dismiss();
                            activity.finish();
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .create().show();
            }
        }
    }

    static void clearCurrentFirestoreData(){
        Gift.sentGifts = new ArrayList<>();
        Gift.receivedGifts = new ArrayList<>();
        Request.sentRequests = new ArrayList<>();
        Request.receivedRequests = new ArrayList<>();
        User.friends = new ArrayList<>();
        User.currentUser = null;
    }
}
