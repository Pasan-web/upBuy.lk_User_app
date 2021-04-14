package com.lk.userapp.Model;

public class Merchant {
    String catDocId;
    String merchantName;
    String time;
    String date;
    String imgUrl;
    String isActive;

    public Merchant() {

    }

    public Merchant(String catDocId, String merchantName, String time, String date, String imgUrl, String isActive) {
        this.catDocId = catDocId;
        this.merchantName = merchantName;
        this.time = time;
        this.date = date;
        this.imgUrl = imgUrl;
        this.isActive = isActive;
    }

    public String getCatDocId() {
        return catDocId;
    }

    public void setCatDocId(String catDocId) {
        this.catDocId = catDocId;
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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
