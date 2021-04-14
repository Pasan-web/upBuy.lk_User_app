package com.lk.userapp.Model;

public class Product {
    String catDoc;
    String merchantDoc;
    String productName;
    String description;
    String price;
    String date;
    String time;
    String isActive;
    String imgUrl;
    double doublePrice;

    public Product() {

    }

    public Product(String catDoc, String merchantDoc, String productName, String description, String price, String date, String time, String isActive, String imgUrl, double doublePrice) {
        this.catDoc = catDoc;
        this.merchantDoc = merchantDoc;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.date = date;
        this.time = time;
        this.isActive = isActive;
        this.imgUrl = imgUrl;
        this.doublePrice = doublePrice;
    }

    public double getDoublePrice() {
        return doublePrice;
    }

    public void setDoublePrice(double doublePrice) {
        this.doublePrice = doublePrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatDoc() {
        return catDoc;
    }

    public void setCatDoc(String catDoc) {
        this.catDoc = catDoc;
    }

    public String getMerchantDoc() {
        return merchantDoc;
    }

    public void setMerchantDoc(String merchantDoc) {
        this.merchantDoc = merchantDoc;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
