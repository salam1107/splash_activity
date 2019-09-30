package com.example.SafeChat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.SafeChat.AppObjects.ChatMessage;
import com.example.SafeChat.adapters.ChatMessagesListAdapter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "1211" ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("msggg","Message Notification Body: " + remoteMessage.getData().get("msg")+","+remoteMessage.getData().get("sender"));
        String msg=remoteMessage.getData().get("msg");
        String sender=remoteMessage.getData().get("sender");
        int type= Integer.parseInt(remoteMessage.getData().get("type"));
        if(type==0){
            try {
                msg=EncryptionHelper.decrypt(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.safechatlogo)
                .setContentTitle(sender)
                .setContentText(type==1?"send you picture ":msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(111, builder.build());
        if(type==0 || type==1) {

            Intent in = new Intent("com.example.SafeChat");
            Bundle extras = new Bundle();
            extras.putString("com.example.SafeChat.msg", msg);
            extras.putInt("com.example.SafeChat.type", type);
            in.putExtras(extras);
            getApplicationContext().sendBroadcast(in);
            ChatMessage chatMessage = new ChatMessage(sender, msg,type);
            try {
                ArrayList<ChatMessage> list = readChateFromFile(sender);
                list.add(chatMessage);
                writeChatToFile(list, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    private void writeChatToFile(ArrayList<ChatMessage> list,String senderUserName) throws IOException {
        String filename = senderUserName+".txt";
        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(list);
        os.close();
        fos.close();
    }
    private ArrayList<ChatMessage>  readChateFromFile(String senderUserName) throws Exception {
        String filename = senderUserName+".txt";
        FileInputStream fi = openFileInput(filename);
        ObjectInputStream oi = new ObjectInputStream(fi);
        ArrayList<ChatMessage> list= (ArrayList<ChatMessage>) oi.readObject();
        return list;
    }
    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("tokeeen",s);
        super.onNewToken(s);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "chat messages";
            String description = "chat messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
