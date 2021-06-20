package com.mobcom.chatapp.model;

import com.google.gson.annotations.SerializedName;

public class RequestNotification {
    @SerializedName("token") //  "to" changed to token
    private String token;

    @SerializedName("notification")
    private SendNotification sendNotification;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SendNotification getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(SendNotification sendNotification) {
        this.sendNotification = sendNotification;
    }
}
