package com.lk.userapp.Model;

public class SliderModel {
    private int id;
    private String imgUrl;

    public SliderModel(int id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public SliderModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
