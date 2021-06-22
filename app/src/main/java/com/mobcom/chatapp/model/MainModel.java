package com.mobcom.chatapp.model;

import com.google.gson.annotations.SerializedName;

public class MainModel {

    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private Notification notification;

    @SerializedName("data")
    private Data data;

    public MainModel(String token, Notification notification, Data data) {
        this.token = token;
        this.notification = notification;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
