package com.example.tim.coinz;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User{
    private String userId;
    //private double goldValue;
    private String name;
    static User currentUser;
    static ArrayList<User> friends = new ArrayList<>();
    static Double walkingDistance = 0.0;
    private static ListenerRegistration friendDeleteListener;
    private static final String TAG = "USER";

    public User(String userId, String name){
        this.userId = userId;
        this.name = name;
    }

    String getUserId() {
        return userId;
    }

    String getName() {
        return name;
    }

    static User findFriendById(String userId) {
        for (User user : friends){
            if (userId.equals(user.userId)){
                return user;
            }
        }
        return null;
    }

    static ArrayList<User> filterFriendsBySentGift(){
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
        db.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(collectionReference.document(request.getSenderId()));
            transaction.update(collectionReference.document(User.currentUser.getUserId()), "FriendList", FieldValue.arrayUnion(collectionReference.document(request.getSenderId())));
            transaction.update(collectionReference.document(request.getSenderId()), "FriendList", FieldValue.arrayUnion(collectionReference.document(User.currentUser.getUserId())));
            transaction.update(db.collection("FRIEND_REQUEST").document(request.getRequestId()), "Status", Request.ACCEPT);
            return documentSnapshot;
        }).addOnSuccessListener(documentSnapshot -> {
            requestListAdapter.removeItem(position);
            friendListAdapter.addItem(new User(documentSnapshot.getId(), documentSnapshot.getString("Name")));
        }).addOnFailureListener(e -> {
            // TODO handle failure
            Log.w(TAG, e);
        });
    }

    static void rejectFriendRequest(RequestListAdapter adapter, Request request, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FRIEND_REQUEST").document(request.getRequestId()).update("Status", Request.DENY)
                .addOnSuccessListener(aVoid -> adapter.removeItem(position))
                .addOnFailureListener(e -> {
                    // TODO handle failure
                    Log.w(TAG, e);
                });
    }

    static void deleteFriend(User friend) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USER");
        WriteBatch writeBatch = db.batch();
        writeBatch.update(collectionReference.document(friend.getUserId()), "FriendList",FieldValue.arrayRemove(collectionReference.document(User.currentUser.getUserId())));
        writeBatch.update(collectionReference.document(User.currentUser.getUserId()), "FriendList",FieldValue.arrayRemove(collectionReference.document(friend.getUserId())));
        writeBatch.commit().addOnFailureListener(e -> Log.w(TAG, e));
    }

    static void updateFriendListOnRequestAccept(String receiverId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USER").document(receiverId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        FriendListAdapter friendListAdapter = FriendListAdapter.getCurrentAdapter();
                        FriendSelectListAdapter friendSelectListAdapter = FriendSelectListAdapter.getCurrentAdapter();
                        User newFriend = new User(documentSnapshot.getId(), documentSnapshot.getString("Name"));
                        if (friendListAdapter != null) {
                            friendListAdapter.addItem(newFriend);
                            //TODO update friend select adapter
                        } else {
                            friends.add(newFriend);
                        }

                        if (friendSelectListAdapter != null){
                            friendSelectListAdapter.addItem(newFriend);
                        }
                    } catch (NullPointerException ex){
                        Log.w(TAG, ex);
                    }
                })
                .addOnFailureListener(e -> {
                    //TODO handle failure
                    Log.w(TAG, e);
                });
    }

    static void addFriendDeleteListener(){
        if (friendDeleteListener != null) friendDeleteListener.remove();
        // reattach
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USER");
        friendDeleteListener = collectionReference.whereArrayContains("FriendList", collectionReference.document(User.currentUser.getUserId()))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                        if (dc.getType().equals(DocumentChange.Type.REMOVED)) {
                            User user = findFriendById(dc.getDocument().getId());
                            if (user == null) {
                                Log.w(TAG, "delete friend not found");
                                return;
                            }
                            int position = friends.indexOf(user);
                            FriendListAdapter adapter = FriendListAdapter.getCurrentAdapter();
                            if (adapter != null) {
                                adapter.removeItem(position);
                            } else {
                                friends.remove(position);
                            }
                            FriendSelectListAdapter friendSelectListAdapter = FriendSelectListAdapter.getCurrentAdapter();
                            if (friendSelectListAdapter != null) {
                                friendSelectListAdapter.removeItemById(user.userId);
                            }
                        }
                    }
                });
    }

    static void detachAllListener(){
        if (friendDeleteListener != null) friendDeleteListener.remove();
    }
}
