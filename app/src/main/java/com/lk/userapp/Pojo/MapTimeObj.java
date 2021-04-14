package com.lk.userapp.Pojo;

public class MapTimeObj {
    int timeInMins;
    String timeInText;

    public MapTimeObj() {

    }

    public MapTimeObj(int timeInMins, String timeInText) {
        this.timeInMins = timeInMins;
        this.timeInText = timeInText;
    }

    public int getTimeInMins() {
        return timeInMins;
    }

    public void setTimeInMins(int timeInMins) {
        this.timeInMins = timeInMins;
    }

    public String getTimeInText() {
        return timeInText;
    }

    public void setTimeInText(String timeInText) {
        this.timeInText = timeInText;
    }
}
