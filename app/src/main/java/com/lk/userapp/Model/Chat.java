package com.lk.userapp.Model;

import java.util.Date;

public class Chat {
    String userDoc;
    Date datetime;
    String status;

    public Chat(String userDoc, Date datetime, String status) {
        this.userDoc = userDoc;
        this.datetime = datetime;
        this.status = status;
    }

    public Chat() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(String userDoc) {
        this.userDoc = userDoc;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}

