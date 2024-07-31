package com.example.movadmin.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String userId;
    private String username;
    private String phoneNo;
    private String profilePic;
    private int numOfOrders;
    private double totalKms;

    public UserModel(String userId, String username, String phoneNo, String profilePic, int numOfOrders, double totalKms) {
        this.userId = userId;
        this.username = username;
        this.phoneNo = phoneNo;
        this.profilePic = profilePic;
        this.numOfOrders = numOfOrders;
        this.totalKms = totalKms;
    }

    public UserModel() {}

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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
}
