package com.example.SafeChat.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.example.SafeChat.AppObjects.FriendRequest;
import com.example.SafeChat.FirebaseDbController;
import com.example.SafeChat.LoginActivity;
import com.example.SafeChat.R;
import com.example.SafeChat.sendMessageAsyncTask;

import java.util.ArrayList;

//FriendRequestListAdapter
public class FriendRequestListAdapter extends ArrayAdapter<Friend> {
    ArrayList<Friend> requests;
    Context context;


    public FriendRequestListAdapter(@NonNull Context context, @NonNull ArrayList<Friend> objects) {
        super(context, R.layout.friend_listview_item, objects);
        requests =objects;
        this.context=context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.friend_request_listview_item, parent, false);
        final Friend f = requests.get(position);


        final ImageView profileImageView=(ImageView)view.findViewById(R.id.profileImageView);
        TextView userNameTxt=(TextView)view.findViewById(R.id.userNameTxt);
        TextView fullNameTxt=(TextView)view.findViewById(R.id.fullNameTxt);
        Button accBtn=view.findViewById(R.id.accBtn);
        Button rejBtn=view.findViewById(R.id.rejbtn);
        userNameTxt.setText(f.getUserName());
        fullNameTxt.setText(f.getFullname());
        AppCallBack callBack=new AppCallBack() {
            @Override
            public void callback(Object o) {
                //Todo: cache users profile images
                Bitmap bitmap= (Bitmap) o;
                profileImageView.setImageBitmap(bitmap);
            }
        };
        new FirebaseDbController().downloadProfileImage(f.getUserName(),callBack);
        final View finalView = view;
        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseDbController().addFriend(LoginActivity.loginUser.getUsername(),f);
                finalView.setVisibility(View.GONE);
                new sendMessageAsyncTask("accept your friend request",f.getToken(), LoginActivity.loginUser.getUsername(),3).execute();
            }
        });
        rejBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FirebaseDbController().removeFriendRequest(LoginActivity.loginUser.getUsername(),f.getUserName());
                finalView.setVisibility(View.GONE);
            }
        });

        return view;
    }
}
