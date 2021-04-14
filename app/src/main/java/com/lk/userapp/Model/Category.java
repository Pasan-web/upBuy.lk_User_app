package com.lk.userapp.Model;

public class Category {
    private String categoryId;
    private String categoryName;
    private String imgUrl;
    private String isActive;

    public Category(String categoryId, String categoryName, String imgUrl, String isActive) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imgUrl = imgUrl;
        this.isActive = isActive;
    }

    public Category() {

    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
