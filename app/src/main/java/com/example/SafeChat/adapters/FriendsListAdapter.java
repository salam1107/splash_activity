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
import com.example.SafeChat.AppObjects.Friend;
import com.example.SafeChat.ChatActivity;
import com.example.SafeChat.FirebaseDbController;
import com.example.SafeChat.LoginActivity;
import com.example.SafeChat.R;
import com.example.SafeChat.sendMessageAsyncTask;

import java.util.ArrayList;


/**
 * Created by mohna on 20/08/2017.
 */

public class FriendsListAdapter extends ArrayAdapter<Friend> {
    ArrayList<Friend> friends;
    Context context;
    boolean searchMode;

    public FriendsListAdapter(@NonNull Context context, @NonNull ArrayList<Friend> objects,boolean searchMode) {
        super(context, R.layout.friend_listview_item, objects);
        friends =objects;
        this.context=context;
        this.searchMode=searchMode;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.friend_listview_item, parent, false);
        final Friend f = friends.get(position);


        final ImageView profileImageView=(ImageView)view.findViewById(R.id.profileImageView);
        TextView userNameTxt=view.findViewById(R.id.userNameTxt);
        TextView fullNameTxt=view.findViewById(R.id.fullNameTxt);
        Button chatBtn=view.findViewById(R.id.chatBtn);
        userNameTxt.setText(f.getUserName());
        fullNameTxt.setText(f.getFullname());
        AppCallBack callBack=new AppCallBack() {
            @Override
            public void callback(Object o) {
                Bitmap bitmap= (Bitmap) o;
                if(bitmap!=null)
                    profileImageView.setImageBitmap(bitmap);
            }
        };
        new FirebaseDbController().downloadProfileImage(f.getUserName(),callBack);
        if (searchMode){
            chatBtn.setText("send request");
        }
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!searchMode) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("userName", f.getUserName());
                    intent.putExtra("fToken", f.getToken());
                    context.startActivity(intent);
                }else
                {
                    new FirebaseDbController().sendFriendRequest(f.getUserName());
                    new sendMessageAsyncTask("send you friend request",f.getToken(), LoginActivity.loginUser.getUsername(),3).execute();
                }
            }
        });
        return view;
    }
}
