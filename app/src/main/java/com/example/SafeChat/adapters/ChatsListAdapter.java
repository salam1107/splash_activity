package com.example.SafeChat.adapters;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.Chat;
import com.example.SafeChat.ChatActivity;
import com.example.SafeChat.FirebaseDbController;
import com.example.SafeChat.R;

import java.util.ArrayList;


/**
 * Created by mohna on 20/08/2017.
 */

public class ChatsListAdapter extends ArrayAdapter<Chat> {
    ArrayList<Chat> chats;
    Context context;


    public ChatsListAdapter(@NonNull Context context, @NonNull ArrayList<Chat> objects) {
        super(context, R.layout.chat_listview_item, objects);
        chats =objects;
        this.context=context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.chat_listview_item, parent, false);
        final Chat c = chats.get(position);


        final ImageView profileImage=(ImageView)view.findViewById(R.id.profileImageView);
        TextView userName=(TextView)view.findViewById(R.id.userNameTxt);
        TextView lastMsgTxt=(TextView)view.findViewById(R.id.lastMsgTxt);
        Button chatBtn=view.findViewById(R.id.chatBtn);
        userName.setText(c.getUserName());
        lastMsgTxt.setText(c.getLastMsg());
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("userName",c.getUserName());
                intent.putExtra("fToken",c.getToken());
                context.startActivity(intent);
            }
        });
        AppCallBack callBack=new AppCallBack() {
            @Override
            public void callback(Object o) {
                Bitmap bitmap= (Bitmap) o;
                if(bitmap!=null)
                    profileImage.setImageBitmap(bitmap);
            }
        };
        new FirebaseDbController().downloadProfileImage(c.getUserName(),callBack);
        return view;
    }
}
