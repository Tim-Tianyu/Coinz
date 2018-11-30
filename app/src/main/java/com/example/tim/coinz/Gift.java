package com.example.tim.coinz;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Gift{
    private String giftId;
    private Double value;
    private Boolean received;
    private String senderId;
    private String receiverId;
    private Timestamp timestamp;

    public Gift(String giftId, Double value, Boolean recevied, String senderId, String receiverId, Timestamp timestamp) {
        this.giftId = giftId;
        this.value = value;
        this.received = recevied;
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
}
