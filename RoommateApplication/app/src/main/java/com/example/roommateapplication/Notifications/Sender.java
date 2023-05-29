package com.example.roommateapplication.Notifications;

public class Sender {
    private Data data;
    private String to;

    public Sender() {
        // Empty constructor required for Firebase Realtime Database
    }

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
