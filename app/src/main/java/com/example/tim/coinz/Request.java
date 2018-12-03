package com.example.tim.coinz;

import com.google.firebase.Timestamp;

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
    private Timestamp timestamp;

    private static final Double PENDING = 0.0;
    private static final Double ACCEPT = 1.0;
    private static final Double DENY = 2.0;
    static ArrayList<Request> sentRequests = new ArrayList<>();
    static ArrayList<Request> receivedRequests = new ArrayList<>();

    public Request(String requestId, String senderId, String receiverId, Status status, Timestamp timestamp) {
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.status = status;
        this.timestamp = timestamp;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    static Status DoubleToStatus(Double num) {
        if (num.equals(PENDING)) return Status.PENDING;
        else if (num.equals(ACCEPT)) return Status.ACCEPT;
        else if (num.equals(DENY)) return Status.PENDING;
        else return null;
    }

    static Double StatusToDouble(Status status){
        if (status.equals(Status.ACCEPT)) return ACCEPT;
        else if (status.equals(Status.DENY)) return DENY;
        else if (status.equals(Status.PENDING)) return PENDING;
        else return null; // this should be impossible
    }

    static Request findRequestById (ArrayList<Request> requestList, String requestId) {
        for (Request request : requestList){
            if (requestId.equals(request.requestId)){
                return request;
            }
        }
        return null;
    }

    static Request findReceivedRequestBySenderId(String senderId) {
        for (Request request : receivedRequests){
            if (senderId.equals(request.senderId)){
                return request;
            }
        }
        return null;
    }

    static Request findSentRequestByReceiverId(String receiverId){
        for (Request request : sentRequests){
            if (receiverId.equals(request.receiverId)){
                return request;
            }
        }
        return null;
    }

}
