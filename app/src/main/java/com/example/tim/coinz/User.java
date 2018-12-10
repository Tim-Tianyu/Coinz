package com.example.tim.coinz;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class User{
    private String userId;
    private String name;
    // current log in user
    static User currentUser;
    // static friend list
    static ArrayList<User> friends = new ArrayList<>();
    // current user's walking distance
    static Double walkingDistance = 0.0;
    // listener for firestore
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
        // return list of friends that user haven't sent gift to
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
        // update friend request status and friend list on firestore on accept friend request
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USER");
        // create transaction for multiple read and write
        db.runTransaction(transaction -> {
            DocumentSnapshot documentSnapshot = transaction.get(collectionReference.document(request.getSenderId()));
            transaction.update(collectionReference.document(User.currentUser.getUserId()), "FriendList", FieldValue.arrayUnion(collectionReference.document(request.getSenderId())));
            transaction.update(collectionReference.document(request.getSenderId()), "FriendList", FieldValue.arrayUnion(collectionReference.document(User.currentUser.getUserId())));
            transaction.update(db.collection("FRIEND_REQUEST").document(request.getRequestId()), "Status", Request.ACCEPT);
            return documentSnapshot;
        }).addOnSuccessListener(documentSnapshot -> {
            requestListAdapter.removeItem(position);
            friendListAdapter.addItem(new User(documentSnapshot.getId(), documentSnapshot.getString("Name")));
        }).addOnFailureListener(e -> Log.w(TAG, e));
    }

    static void rejectFriendRequest(RequestListAdapter adapter, Request request, int position){
        // update friend request status on accept friend request
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("FRIEND_REQUEST").document(request.getRequestId()).update("Status", Request.DENY)
                .addOnSuccessListener(aVoid -> adapter.removeItem(position))
                .addOnFailureListener(e -> Log.w(TAG, e));
    }

    static void deleteFriend(User friend){
        // update friend list on firestore on delete friend
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USER");
        // use writeBatch for multiple write
        WriteBatch writeBatch = db.batch();
        writeBatch.update(collectionReference.document(friend.getUserId()), "FriendList",FieldValue.arrayRemove(collectionReference.document(User.currentUser.getUserId())));
        writeBatch.update(collectionReference.document(User.currentUser.getUserId()), "FriendList",FieldValue.arrayRemove(collectionReference.document(friend.getUserId())));
        writeBatch.commit().addOnFailureListener(e -> Log.w(TAG, e));
    }

    static void updateFriendListOnRequestAccept(String receiverId){
        // when sent request accept by other user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("USER").document(receiverId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    FriendSelectListAdapter friendSelectListAdapter = FriendSelectListAdapter.getCurrentAdapter();
                    User newFriend = new User(documentSnapshot.getId(), documentSnapshot.getString("Name"));

                    // adapter will be null if it don't have focus currently
                    FriendListAdapter friendListAdapter = FriendListAdapter.getCurrentAdapter();
                    if (friendListAdapter != null) {
                        // update adapter as it has focus
                        friendListAdapter.addItem(newFriend);
                    } else {
                        friends.add(newFriend);
                    }

                    if (friendSelectListAdapter != null){
                        friendSelectListAdapter.addItem(newFriend);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, e));
    }

    static void addFriendDeleteListener(){
        // listener to listen for deletion of user as a friend from another user
        if (friendDeleteListener != null) friendDeleteListener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("USER");
        friendDeleteListener = collectionReference.whereArrayContains("FriendList", collectionReference.document(User.currentUser.getUserId()))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    // get the list of friend that have deletion on their document
                    ArrayList<String> friendIdListWithDocumentChange = new ArrayList<>();
                    for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()){
                        if (dc.getType().equals(DocumentChange.Type.REMOVED)) {
                            friendIdListWithDocumentChange.add(dc.getDocument().getId());
                        }
                    }

                    // check if current user's friend list still have these friends
                    db.collection("USER").document(User.currentUser.getUserId()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                List<DocumentReference> documentList = (List<DocumentReference>) documentSnapshot.get("FriendList");

                                // get current friend list
                                ArrayList<String> friendIdList = new ArrayList<>();
                                for (DocumentReference documentReference : Objects.requireNonNull(documentList)){
                                    friendIdList.add(documentReference.getId());
                                }

                                for (String friendId : friendIdListWithDocumentChange) {
                                    // if don't have that friend in current friend update local friend list (remove that friend)
                                    if ( !friendIdList.contains(friendId)) {
                                        User user = findFriendById(friendId);
                                        if (user == null) {
                                            Log.w(TAG, "delete friend not found");
                                            return;
                                        }
                                        int position = friends.indexOf(user);
                                        // adapter will be null if it don't have focus currently
                                        FriendListAdapter adapter = FriendListAdapter.getCurrentAdapter();
                                        if (adapter != null) {
                                            // update adapter as it has focus
                                            adapter.removeItem(position);
                                        } else {
                                            friends.remove(position);
                                        }
                                        // adapter will be null if it don't have focus currently
                                        FriendSelectListAdapter friendSelectListAdapter = FriendSelectListAdapter.getCurrentAdapter();
                                        if (friendSelectListAdapter != null) {
                                            // update adapter as it has focus
                                            friendSelectListAdapter.removeItemById(user.userId);
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(e1 -> Log.w(TAG, e1));
                });
    }

    static void detachAllListener(){
        if (friendDeleteListener != null) friendDeleteListener.remove();
    }
}
