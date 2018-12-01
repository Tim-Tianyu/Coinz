package com.example.tim.coinz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User{
    private String userId;
    private double goldValue;
    private String name;
    static User currentUser;
    static ArrayList<User> friends = new ArrayList<>();

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

    public static User findUserById (ArrayList<User> userList, String userId) {
        for (User user : userList){
            if (userId.equals(user.userId)){
                return user;
            }
        }
        return null;
    }

    public static ArrayList<User> filterFriendsBySentGift(){
        Set<String> IdList= new HashSet<>();
        ArrayList<User> filtered = new ArrayList<>();
        for (Gift gift : Gift.sentGifts) {
            IdList.add(gift.getReceiverId());
        }
        for (User user : friends) {
            if (!IdList.contains(user.userId)) {
                filtered.add(user);
            }
        }
        return filtered;
    }
}
