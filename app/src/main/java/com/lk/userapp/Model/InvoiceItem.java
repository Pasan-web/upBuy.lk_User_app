package com.lk.userapp.Model;

public class InvoiceItem {
    String productDocId;
    String invoiceDocId;
    String qty;

    public InvoiceItem(String productDocId, String invoiceDocId, String qty) {
        this.productDocId = productDocId;
        this.invoiceDocId = invoiceDocId;
        this.qty = qty;
    }

    public InvoiceItem() {

    }

    public String getProductDocId() {
        return productDocId;
    }

    public void setProductDocId(String productDocId) {
        this.productDocId = productDocId;
    }

    public String getInvoiceDocId() {
        return invoiceDocId;
    }

    public void setInvoiceDocId(String invoiceDocId) {
        this.invoiceDocId = invoiceDocId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
