package com.mobcom.chatapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class MainModel {

    @SerializedName("to")
    private String to;

    @SerializedName("notification")
    private Notification notification;

    @SerializedName("data")
    private Map<String, String> data;


    public MainModel(String to, Notification notification, Map<String, String> data) {
        this.to = to;
        this.notification = notification;
        this.data = data;

    }

    public String getToken() {
        return to;
    }

    public void setToken(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
