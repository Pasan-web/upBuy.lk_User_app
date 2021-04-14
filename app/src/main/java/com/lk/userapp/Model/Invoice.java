package com.lk.userapp.Model;

public class Invoice {
    String userDocId;
    String addressDocId;
    String date;
    String time;
    String amount;
    String status;

    public Invoice(String userDocId, String addressDocId, String date, String time, String amount, String status) {
        this.userDocId = userDocId;
        this.addressDocId = addressDocId;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.status = status;
    }

    public Invoice() {

    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getAddressDocId() {
        return addressDocId;
    }

    public void setAddressDocId(String addressDocId) {
        this.addressDocId = addressDocId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
