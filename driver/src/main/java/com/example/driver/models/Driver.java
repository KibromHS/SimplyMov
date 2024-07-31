package com.example.driver.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class Driver implements Serializable {
    private String driverId;
    private String driverName;
    private String phoneNumber;
    private String driverEmail;
    private String password;
    private String driverPic;
    private String driverStatus;
    private String truckSize;
    private int numOfOrders;
    private double totalKms;
    private double rating;
    private int numOfRates;

    public Driver(String driverId, String driverName, String phoneNumber, String driverEmail, String password, String driverPic, String driverStatus, String truckSize, int numOfOrders, double totalKms, double rating, int numOfRates) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverEmail = driverEmail;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.driverPic = driverPic;
        this.driverStatus = driverStatus;
        this.truckSize = truckSize;
        this.numOfOrders = numOfOrders;
        this.totalKms = totalKms;
        this.rating = rating;
        this.numOfRates = numOfRates;
    }

    public Driver() {}

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    public String getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(String driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getTruckSize() {
        return truckSize;
    }

    public void setTruckSize(String truckSize) {
        this.truckSize = truckSize;
    }

    public int getNumOfOrders() {
        return numOfOrders;
    }

    public void setNumOfOrders(int numOfOrders) {
        this.numOfOrders = numOfOrders;
    }

    public double getTotalKms() {
        return totalKms;
    }

    public void setTotalKms(double totalKms) {
        this.totalKms = totalKms;
    }

    public double getRating() {
        return rating;
    }

    public int getNumOfRates() {
        return numOfRates;
    }

    public void setNumOfRates(int numOfRates) {
        this.numOfRates = numOfRates;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Driver fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Driver.class);
    }
}
