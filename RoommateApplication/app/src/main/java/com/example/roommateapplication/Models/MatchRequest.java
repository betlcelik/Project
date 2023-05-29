package com.example.roommateapplication.Models;

public class MatchRequest {
    private String sendingUid;
    private String receivingUid;
    private String state;

    public MatchRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(MatchRequest.class)
    }

    public MatchRequest(String sendingUid, String receivingUid, String state) {
        this.sendingUid = sendingUid;
        this.receivingUid = receivingUid;
        this.state = state;
    }

    public String getSendingUid() {
        return sendingUid;
    }

    public void setSendingUid(String sendingUid) {
        this.sendingUid = sendingUid;
    }

    public String getReceivingUid() {
        return receivingUid;
    }

    public void setReceivingUid(String receivingUid) {
        this.receivingUid = receivingUid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
