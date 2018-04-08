package com.example.android.baitbite.Model;

public class Rate {
    private String customerPhone;
    private String dishID;
    private String rateValue;
    private String comment;

    public Rate() {
    }

    public Rate(String customerPhone, String dishID, String rateValue, String comment) {
        this.customerPhone = customerPhone;
        this.dishID = dishID;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getDishID() {
        return dishID;
    }

    public void setDishID(String dishID) {
        this.dishID = dishID;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
