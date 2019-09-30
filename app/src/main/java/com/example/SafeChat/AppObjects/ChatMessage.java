package com.example.SafeChat.AppObjects;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    public String userName;
    public String msg;
    public int type;//0--msg  1---pic
    public byte[] pic=null;
    public ChatMessage(String userName, String msg,int type) {
        this.userName = userName;
        this.msg = msg;
        this.type=type;
    }

    @NonNull
    @Override
    public String toString() {
        return "userName:" + userName+ "\nmsg: " + msg+ "\ntype: " + type;
    }
}
