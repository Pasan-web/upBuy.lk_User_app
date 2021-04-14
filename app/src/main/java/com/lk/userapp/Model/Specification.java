package com.lk.userapp.Model;

public class Specification {
    String type;
    String value;
    String productDocId;

    public Specification(String type, String value, String productDocId) {
        this.type = type;
        this.value = value;
        this.productDocId = productDocId;
    }

    public Specification() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }
}
