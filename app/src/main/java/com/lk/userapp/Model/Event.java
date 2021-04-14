package com.lk.userapp.Model;

public class Event {
    String merchantName;
    String title;
    String description;
    String imgUrl;
    double latitute;
    double longitute;
    String time;
    String date;
    String status;

    public Event(String merchantName, String title, String description, String imgUrl, double latitute, double longitute, String time, String date, String status) {
        this.merchantName = merchantName;
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.latitute = latitute;
        this.longitute = longitute;
        this.time = time;
        this.date = date;
        this.status = status;
    }

    public Event() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getLatitute() {
        return latitute;
    }

    public void setLatitute(double latitute) {
        this.latitute = latitute;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }
}
