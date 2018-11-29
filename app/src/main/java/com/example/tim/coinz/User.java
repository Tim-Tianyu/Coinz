package com.example.tim.coinz;

import java.util.ArrayList;

public class User{
    private String userId;
    private double goldValue;
    private String name;

    public User(String userId, double goldValue, String name){
        this.userId = userId;
        this.goldValue = goldValue;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public double getGoldValue() {
        return goldValue;
    }

    public String getName() {
        return name;
    }
}