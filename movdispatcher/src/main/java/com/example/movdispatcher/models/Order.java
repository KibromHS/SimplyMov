package com.example.movdispatcher.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String driverId;
    private String from;
    private String to;
    private String itemType;
    private String size;
    private transient Timestamp dateTime;
    private double kms;
    private double price;
    private OrderStatus status;
    private boolean includesPacking;
    private int rate;
    private long dateTimeMillis;

    public Order(String orderId, String userId, String from, String to, String type, String size, Timestamp dateTime, double kms, double price, OrderStatus status, boolean includesPacking, int rate) {
        this.orderId = orderId;
        this.userId = userId;
        this.from = from;
        this.to = to;
        this.itemType = type;
        this.size = size;
        this.dateTime = dateTime;
        this.kms = kms;
        this.price = price;
        this.status = status;
        this.includesPacking = includesPacking;
        this.rate = rate;
    }

    public Order() {}

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
        this.dateTimeMillis = dateTime.toDate().getTime();
    }

    public void setDateTimeMillis(long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
        this.dateTime = new Timestamp(new java.util.Date(dateTimeMillis));
    }

    public double getKms() {
        return kms;
    }

    public void setKms(double kms) {
        this.kms = kms;
    }

    public String getWaitingTime() {
        double hours = getKms() / 10;
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(hours) + " hrs";
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isIncludesPacking() {
        return includesPacking;
    }

    public void setIncludesPacking(boolean includesPacking) {
        this.includesPacking = includesPacking;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
