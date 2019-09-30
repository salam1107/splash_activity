package com.example.SafeChat.AppObjects;

public class FriendRequest {
    private String userName;
    private String fullname;

    public FriendRequest() {
    }

    public FriendRequest(String userName, String fullname) {
        this.userName = userName;
        this.fullname = fullname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
