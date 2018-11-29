package com.example.tim.coinz;

import java.util.ArrayList;

public class Gift{
    private String giftId;
    private Double value;
    private Boolean received;
    private String senderId;
    private String receiverId;

    public Gift(String giftId, Double value, Boolean recevied, String senderId, String receiverId) {
        this.giftId = giftId;
        this.value = value;
        this.received = recevied;
        this.senderId = senderId;
        this.receiverId = receiverId;
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
}
