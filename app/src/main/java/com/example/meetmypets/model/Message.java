package com.example.meetmypets.model;

import java.util.Map;

public class Message {

    private Map<String, String> time;
    private String senderUid;
    private String message;
    private int type;

    public Message() {
    }

    public Message(Map<String, String> time, String senderUid, String message, int type) {
        this.time = time;
        this.senderUid = senderUid;
        this.message = message;
        this.type = type;
    }

    public Map<String, String> getTime() {
        return time;
    }

    public void setTime(Map<String, String> time) {
        this.time = time;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}