package com.lk.userapp.Model;

public class WishList {
    String userDocId;
    String productDocId;
    String time;
    String date;

    public WishList(String userDocId, String productDocId, String time, String date) {
        this.userDocId = userDocId;
        this.productDocId = productDocId;
        this.time = time;
        this.date = date;
    }

    public WishList() {

    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String producrDocId) {
        this.productDocId = producrDocId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
