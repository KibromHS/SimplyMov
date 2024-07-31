package com.example.movdispatcher.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class Dispatcher implements Serializable {
    private String dispatcherId;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String imgUrl;

    public Dispatcher() {}

    public Dispatcher(String dispatcherId, String name, String phoneNumber, String email, String password, String imgUrl) {
        this.dispatcherId = dispatcherId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.imgUrl = imgUrl;
    }

    public String getDispatcherId() {
        return dispatcherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Dispatcher fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Dispatcher.class);
    }
}
