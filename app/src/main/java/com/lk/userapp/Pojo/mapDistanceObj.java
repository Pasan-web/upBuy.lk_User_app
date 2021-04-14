package com.lk.userapp.Pojo;

public class mapDistanceObj {
    String distanceText;
    int distanceValue;

    public mapDistanceObj(String distanceText, int distanceValue) {
        this.distanceText = distanceText;
        this.distanceValue = distanceValue;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }
}
