package com.shra1.fcmnotification.models;

public class User
{
    String name;
    String fcmToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public User() {

    }

    public User(String name, String fcmToken) {

        this.name = name;
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return getName();
    }
}
