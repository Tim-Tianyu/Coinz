package com.example.tim.coinz;

import java.util.ArrayList;

public class Request {
    enum Status {
        PENDING,
        ACCEPT,
        DENY
    }

    private String requestId;
    private String senderId;
    private String receiverId;
    private Status status;

    private static final Double PENDING = 0.0;
    private static final Double ACCEPT = 1.0;
    private static final Double DENY = 2.0;

    public Request(String requestId, String senderId, String receiverId, Status status) {
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public Status getStatus() {
        return status;
    }

    public static Status DoubleToStatus(Double num) {
        if (num.equals(PENDING)) return Status.PENDING;
        else if (num.equals(ACCEPT)) return Status.ACCEPT;
        else if (num.equals(DENY)) return Status.PENDING;
        else return null;
    }

    public static Double StatusToDouble(Status status){
        if (status.equals(Status.ACCEPT)) return ACCEPT;
        else if (status.equals(Status.DENY)) return DENY;
        else if (status.equals(Status.PENDING)) return PENDING;
        else return null; // this should be impossible
    }
}