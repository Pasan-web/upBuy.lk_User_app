package com.lk.userapp.Model;

public class Add {
    private String date;
    private String time;
    private String imgUrl;

    public Add() {

    }

    public Add(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Add(String date, String time, String imgUrl) {
        this.date = date;
        this.time = time;
        this.imgUrl = imgUrl;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
