package com.example.SafeChat.AppObjects;

import java.util.ArrayList;

public class User {
    private String fullname;
    private String username;
    private String password;
    private ArrayList<Friend> requests;
    private ArrayList<Friend> friends;
    public User(){

    }

    public User(String fullname, String username, String password) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Friend> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Friend> requests) {
        this.requests = requests;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }
}
