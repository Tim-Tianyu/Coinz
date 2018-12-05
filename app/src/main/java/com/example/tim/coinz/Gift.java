package com.example.tim.coinz;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;

public class Gift{
    private String giftId;
    private Double value;
    private Boolean received;
    private String senderId;
    private String receiverId;
    private Timestamp timestamp;
    static ArrayList<Gift> sentGifts = new ArrayList<>();
    static ArrayList<Gift> receivedGifts = new ArrayList<>();
    private static ListenerRegistration receiveGiftListener;
    private static ListenerRegistration sentGiftReceivedListener;
    private static String TAG = "Gift";

    public Gift(String giftId, Double value, Boolean received, String senderId, String receiverId, Timestamp timestamp) {
        this.giftId = giftId;
        this.value = value;
        this.received = received;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
    }

    public Boolean isReceived() {
        return received;
    }

    public Double getValue() {
        return value;
    }

    public String getGiftId() {
        return giftId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public static Gift findGiftByGiftId(ArrayList<Gift> giftList, String giftId) {
        for (Gift gift : giftList) {
            if (giftId.equals(gift.giftId)){
                return gift;
            }
        }
        return null;
    }

    static void addReceiveGiftListener(){
        if (receiveGiftListener != null) receiveGiftListener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        receiveGiftListener = db.collection("GIFT").whereEqualTo("Receiver",db.collection("USER").document(User.currentUser.getUserId())).whereEqualTo("IsReceived", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "ReceiveGiftListener:error", e);
                            return;
                        }

                        for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()){
                            if (dc.getType().equals(DocumentChange.Type.ADDED)){
                                QueryDocumentSnapshot snapshot = dc.getDocument();
                                String giftId = snapshot.getId();
                                if (findGiftByGiftId(receivedGifts, giftId) != null) return;
                                ReceiveGiftListAdapter adapter = ReceiveGiftListAdapter.getCurrentAdapter();
                                Gift newGift;
                                try {
                                    newGift = new Gift(snapshot.getId(), snapshot.getDouble("Value"), snapshot.getBoolean("IsReceived"), snapshot.getDocumentReference("Sender").getId(), snapshot.getDocumentReference("Receiver").getId(), snapshot.getTimestamp("Time"));
                                } catch (NullPointerException ex) {
                                    Log.w(TAG, ex);
                                    return;
                                }
                                if (adapter != null) {
                                    adapter.addItem(newGift);
                                } else {
                                    receivedGifts.add(newGift);
                                }
                            }
                        }
                    }
                });
    }

    static void addSentGiftReceivedListener(){
        if (sentGiftReceivedListener != null) sentGiftReceivedListener.remove();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        sentGiftReceivedListener = db.collection("GIFT").whereEqualTo("Sender", db.collection("USER").document(User.currentUser.getUserId()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "SentGiftReceivedListener:error", e);
                            return;
                        }
                        for (DocumentChange dc : Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges()) {
                            if (dc.getType().equals(DocumentChange.Type.MODIFIED)) {
                                QueryDocumentSnapshot snapshot = dc.getDocument();
                                String giftId = snapshot.getId();
                                Gift newGift = findGiftByGiftId(sentGifts, giftId);
                                if (newGift == null){
                                    Log.w(TAG, "sent gift not found");
                                } else {
                                    sentGifts.remove(newGift);
                                }
                            }
                        }
                    }
                });
    }

    static void detachAllListener() {
        if (receiveGiftListener != null) receiveGiftListener.remove();
        if (sentGiftReceivedListener != null) sentGiftReceivedListener.remove();
    }
}
