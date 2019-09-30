package com.example.SafeChat.AppObjects;

public class Chat {
    private String userName;
    private String token;
    private String lastMsg;

    public Chat(String userName, String token, String lastMsg) {
        this.userName = userName;
        this.token = token;
        this.lastMsg = lastMsg;
    }
    public Chat(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
