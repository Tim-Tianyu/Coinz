package com.example.tim.coinz;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User{
    private String userId;
    private double goldValue;
    private String name;
    static User currentUser;
    static ArrayList<User> friends = new ArrayList<>();

    public User(String userId, double goldValue, String name){
        this.userId = userId;
        this.goldValue = goldValue;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public double getGoldValue() {
        return goldValue;
    }

    public String getName() {
        return name;
    }

    public static User findUserById (ArrayList<User> userList, String userId) {
        for (User user : userList){
            if (userId.equals(user.userId)){
                return user;
            }
        }
        return null;
    }

    public static ArrayList<User> filterFriendsBySentGift(){
        Set<String> IdList= new HashSet<>();
        ArrayList<User> filtered = new ArrayList<>();
        for (Gift gift : Gift.sentGifts) {
            IdList.add(gift.getReceiverId());
        }
        for (User user : friends) {
            if (!IdList.contains(user.userId)) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    static void acceptFriendRequest(RequestListAdapter requestListAdapter, FriendListAdapter friendListAdapter, Request request, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USER");
        db.runTransaction(new Transaction.Function<DocumentSnapshot>() {
            @Override
            public DocumentSnapshot apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot documentSnapshot = transaction.get(collectionReference.document(request.getSenderId()));
                transaction.update(collectionReference.document(User.currentUser.getUserId()), "FriendList", FieldValue.arrayUnion(collectionReference.document(request.getSenderId())));
                transaction.update(collectionReference.document(request.getSenderId()), "FriendList", FieldValue.arrayUnion(collectionReference.document(User.currentUser.getUserId())));
                transaction.update(db.collection("FRIEND_REQUEST").document(request.getRequestId()), "Status", Request.StatusToDouble(Request.Status.ACCEPT));
                return documentSnapshot;
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // TODO null pointer handler
                requestListAdapter.removeItem(position);
                friendListAdapter.addItem(new User(documentSnapshot.getId(), documentSnapshot.getDouble("Gold"), documentSnapshot.getString("Name")));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO handle failure
                Log.w("USER", e);
            }
        });
    }

    static void rejectFriendRequest(RequestListAdapter adapter, Request request, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FRIEND_REQUEST").document(request.getRequestId()).update("Status", Request.StatusToDouble(Request.Status.DENY))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        adapter.removeItem(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // TODO handle failure
                        Log.w("USER", e);
                    }
                });

    }
}
