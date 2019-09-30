package com.example.SafeChat;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.ChatMessage;
import com.example.SafeChat.adapters.ChatMessagesListAdapter;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends AppCompatActivity {
    String chatUserName="";
    String fToken="";
    TextView userNameTxt;
    ListView chatListView;
    Button sendChatBtn;
    EditText msgEditTxt;
    CircleImageView profileImageView;
    private BroadcastReceiver broadcastReceiver;
    ArrayList<ChatMessage> list;
    ImageButton shareBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUiComponents();
        setListeners();
        chatUserName=getIntent().getStringExtra("userName");
        fToken=getIntent().getStringExtra("fToken");
        userNameTxt.setText(chatUserName);
        list=new ArrayList<>();
        ChatMessagesListAdapter adapter=new ChatMessagesListAdapter(this,list);
        chatListView.setAdapter(adapter);
        if(list!=null && list.size()!=0){
            chatListView.setSelection(list.size() - 1);
        }
        registerReceiver();
        try {
            readChateFromFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppCallBack appCallBack=new AppCallBack() {
            @Override
            public void callback(Object o) {
                Bitmap bitmap= (Bitmap) o;
                if(bitmap!=null)
                    profileImageView.setImageBitmap(bitmap);
            }
        };
        new FirebaseDbController().downloadProfileImage(chatUserName,appCallBack);
    }
    void initUiComponents(){
        userNameTxt=findViewById(R.id.userNameTxt);
        chatListView=findViewById(R.id.chatListView);
        sendChatBtn=findViewById(R.id.sendChatBtn);
        msgEditTxt=findViewById(R.id.msgEditTxt);
        profileImageView=findViewById(R.id.profileChatImageView);
        shareBtn=findViewById(R.id.shareBtn);
    }
    void setListeners(){
        sendChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msgEditTxt.getText().toString().equals(""))
                    return;
                String encMsg= null;
                try {
                    encMsg = EncryptionHelper.encrypt(msgEditTxt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new sendMessageAsyncTask(encMsg,fToken,LoginActivity.loginUser.getUsername(),0).execute();
                ChatMessage chatMessage=new ChatMessage(LoginActivity.loginUser.getUsername(),msgEditTxt.getText().toString(),0);
                list.add(chatMessage);
                ChatMessagesListAdapter adapter=new ChatMessagesListAdapter(ChatActivity.this,list);
                chatListView.setAdapter(adapter);
                chatListView.setSelection(list.size() - 1);
                msgEditTxt.setText("");
                try {
                    writeChatToFile(list);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                if (EasyPermissions.hasPermissions(ChatActivity.this, galleryPermissions)) {
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    startActivityForResult(intent,1);
                } else {
                    EasyPermissions.requestPermissions(ChatActivity.this, "Access for storage",
                            101, galleryPermissions);
                }
            }
        });
    }
    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("com.example.SafeChat.msg");
                int type = intent.getIntExtra("com.example.SafeChat.type",0);
                Log.d("msggg",msg);

                ChatMessage chatMessage=new ChatMessage(chatUserName,msg,type);
                list.add(chatMessage);
                ChatMessagesListAdapter adapter=new ChatMessagesListAdapter(ChatActivity.this,list);
                chatListView.setAdapter(adapter);
                chatListView.setSelection(list.size() - 1);
                try {
                    writeChatToFile(list);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.example.SafeChat"));
    }
    private void writeChatToFile(ArrayList<ChatMessage> list) throws IOException {
        String filename = chatUserName+".txt";
        FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(list);
        os.close();
        fos.close();
    }
    private void readChateFromFile() throws Exception {
        String filename = chatUserName+".txt";
        FileInputStream fi = openFileInput(filename);
        ObjectInputStream oi = new ObjectInputStream(fi);
        list= (ArrayList<ChatMessage>) oi.readObject();
        ChatMessagesListAdapter adapter=new ChatMessagesListAdapter(ChatActivity.this,list);
        chatListView.setAdapter(adapter);
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 1:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String img = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap bitmap=BitmapFactory.decodeFile(img);
                    bitmap=scaleBitmap(bitmap);
                    String msg=BitMapToString(bitmap);
                    Long tsLong = System.currentTimeMillis()/1000;
                    String fileName = tsLong.toString();
                    Random r = new Random();
                    int rand = r.nextInt(99999999 - 99999) + 99999;
                    fileName=fileName+rand+".jpg";

                    byte[] enB=null;
                    try {
                         enB=encBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new FirebaseDbController().uploadMsgImage(enB,fileName);

                    new sendMessageAsyncTask(fileName,fToken,LoginActivity.loginUser.getUsername(),1).execute();
                    ChatMessage chatMessage=new ChatMessage(LoginActivity.loginUser.getUsername(),fileName,1);
                    ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
                    byte [] b=baos.toByteArray();
                    chatMessage.pic=b;
                    list.add(chatMessage);
                    ChatMessagesListAdapter adapter=new ChatMessagesListAdapter(ChatActivity.this,list);
                    chatListView.setAdapter(adapter);
                    chatListView.setSelection(list.size() - 1);


                    try {
                        writeChatToFile(list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static  Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    private Bitmap scaleBitmap(Bitmap bitmap) {
        int maxWidth=500;
        int maxHeight=500;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {

            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            height = maxHeight;
            width = maxWidth;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return bitmap;
    }
    private byte[] encBitmap(Bitmap bitmap) throws Exception {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        byte [] enB=EncryptionHelper.encryptImage(b);
        return enB;
    }
}
