package com.example.SafeChat.AppObjects;

public class Friend {
    private String userName;
    private String fullname;
    private String token;
    public Friend() {
    }

    public Friend(String userName, String fullname,String token) {
        this.userName = userName;
        this.fullname = fullname;
        this.token=token;
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
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
