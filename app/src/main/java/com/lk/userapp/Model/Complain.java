package com.lk.userapp.Model;

import java.util.Date;

public class Complain {
    String userDocId;
    String invoice;
    String reason;
    String description;
    String productUrl;
    String invoiceUrl;
    Date dateTime;
    String status;

    public Complain(String userDocId, String invoice, String reason, String description, String productUrl, String invoiceUrl, Date dateTime, String status) {
        this.userDocId = userDocId;
        this.invoice = invoice;
        this.reason = reason;
        this.description = description;
        this.productUrl = productUrl;
        this.invoiceUrl = invoiceUrl;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Complain() {

    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
