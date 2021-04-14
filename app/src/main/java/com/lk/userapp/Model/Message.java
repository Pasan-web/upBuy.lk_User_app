package com.lk.userapp.Model;

public class Message {
    String userDocId;
    String message;
    String date;
    String time;
    int listCount;
    String status;


    public Message(String userDocId, String message, String date, String time, int listCount, String status) {
        this.userDocId = userDocId;
        this.message = message;
        this.date = date;
        this.time = time;
        this.listCount = listCount;
        this.status = status;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {

    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
