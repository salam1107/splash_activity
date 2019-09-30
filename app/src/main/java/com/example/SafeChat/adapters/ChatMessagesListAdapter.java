package com.example.SafeChat.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.Chat;
import com.example.SafeChat.AppObjects.ChatMessage;
import com.example.SafeChat.ChatActivity;
import com.example.SafeChat.FirebaseDbController;
import com.example.SafeChat.LoginActivity;
import com.example.SafeChat.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ChatMessagesListAdapter extends ArrayAdapter<ChatMessage> {

    ArrayList<ChatMessage> chat;
    Context context;


    public ChatMessagesListAdapter(@NonNull Context context, @NonNull ArrayList<ChatMessage> objects) {
        super(context, R.layout.chat_message_listview_item, objects);
        chat =objects;
        this.context=context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        view = inflater.inflate(R.layout.chat_message_listview_item, parent, false);
        final ChatMessage c = chat.get(position);

        TextView userMessageTextView=(TextView)view.findViewById(R.id.userMessageTextView);
        TextView senderMessageTextView=(TextView)view.findViewById(R.id.senderMessageTextView);
        final ImageView userImageView=view.findViewById(R.id.userImageView);
        final ImageView senderImageView=view.findViewById(R.id.senderImageView);
        userMessageTextView.setVisibility(View.GONE);
        senderMessageTextView.setVisibility(View.GONE);

        userImageView.setVisibility(View.GONE);
        senderImageView.setVisibility(View.GONE);

        if(c.userName.equals(LoginActivity.loginUser.getUsername()) ){
            if(c.type==0){
                userMessageTextView.setVisibility(View.VISIBLE);
                userMessageTextView.setText(c.msg);
            }
            if(c.type==1){
                userImageView.setVisibility(View.VISIBLE);
                if(c.pic==null){
                    AppCallBack appCallBack=new AppCallBack() {
                        @Override
                        public void callback(Object o) {
                            Bitmap bitmap= (Bitmap) o;
                            if(bitmap!=null) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                                byte[] b = baos.toByteArray();
                                c.pic = b;
                                userImageView.setImageBitmap(bitmap);
                            }
                        }
                    };
                    new FirebaseDbController().downloadMsgImage(c.msg,appCallBack);
                }else{

                    Bitmap bitmap=BitmapFactory.decodeByteArray(c.pic, 0, c.pic.length);
                    userImageView.setImageBitmap(bitmap);
                }
            }
        }else {
            if(c.type==0){
                senderMessageTextView.setVisibility(View.VISIBLE);
                senderMessageTextView.setText(c.msg);
            }
            if(c.type==1){
                senderImageView.setVisibility(View.VISIBLE);
                if(c.pic==null){
                    AppCallBack appCallBack=new AppCallBack() {
                        @Override
                        public void callback(Object o) {
                            Bitmap bitmap= (Bitmap) o;
                            if(bitmap!=null) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                                byte[] b = baos.toByteArray();
                                c.pic = b;
                                senderImageView.setImageBitmap(bitmap);
                            }
                        }
                    };
                    new FirebaseDbController().downloadMsgImage(c.msg,appCallBack);
                }else{
                    Bitmap bitmap=BitmapFactory.decodeByteArray(c.pic, 0, c.pic.length);
                    senderImageView.setImageBitmap(bitmap);
                }
            }
        }
        return view;
    }
}
