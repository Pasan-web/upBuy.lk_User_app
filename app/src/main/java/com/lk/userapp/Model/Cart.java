package com.lk.userapp.Model;

public class Cart {

    String userDocId;
    String productDocId;
    String qty;
    String time;
    String date;


    public Cart() {

    }


    public Cart(String userDocId, String productDocId, String qty, String time, String date) {
        this.userDocId = userDocId;
        this.productDocId = productDocId;
        this.qty = qty;
        this.time = time;
        this.date = date;

    }

    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
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
