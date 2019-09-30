package com.example.SafeChat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.Friend;
import com.example.SafeChat.AppObjects.FriendRequest;
import com.example.SafeChat.AppObjects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseDbController {
    private DatabaseReference databaseReference;
    public FirebaseDbController(){
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }
    public void addNewUser(User user){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", user.getFullname());
        map.put("pass", user.getPassword());
        map.put("token", FirebaseInstanceId.getInstance().getToken());
        databaseReference.child("users").child(user.getUsername()).setValue(map);
    }
    public void checkUserName(String userName, final AppCallBack callBack){
        databaseReference.child("users").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callBack.callback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void login(final String userName, final String password, final AppCallBack callBack){
        databaseReference.child("users").child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user=new User();
                    user.setPassword(dataSnapshot.child("pass").getValue().toString());
                    if(user.getPassword().equals(password))
                    {
                        user.setFullname(dataSnapshot.child("fullname").getValue().toString());
                        getFriendsAndRequests(userName);
                        ArrayList<Friend> friends=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.child("friends").getChildren()) {
                            friends.add(new Friend(postSnapshot.getKey(),postSnapshot.child("fullname").getValue().toString(),postSnapshot.child("token").getValue().toString()));
                        }
                        user.setFriends(friends);

                        String token=FirebaseInstanceId.getInstance().getToken();
                        if(!dataSnapshot.child("token").getValue().toString().equals(token)){
                            databaseReference.child("users").child(userName).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                            for(int i=0;i<friends.size();i++){
                                String friendUserName=friends.get(i).getUserName();
                                databaseReference.child("users").child(friendUserName).child("friends").child(userName).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                            }
                        }

                        callBack.callback(user);
                        return;
                    }

                }
                callBack.callback(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getFriendsAndRequests(final String userName){
        databaseReference.child("users").child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){



                        ArrayList<Friend> requests=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.child("requests").getChildren()) {
                            requests.add(new Friend(postSnapshot.getKey(),postSnapshot.child("fullname").getValue().toString(),postSnapshot.child("token").getValue().toString()));
                        }


                        ArrayList<Friend> friends=new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.child("friends").getChildren()) {
                            friends.add(new Friend(postSnapshot.getKey(),postSnapshot.child("fullname").getValue().toString(),postSnapshot.child("token").getValue().toString()));
                        }
                        if(LoginActivity.loginUser!=null){
                            LoginActivity.loginUser.setFriends(friends);
                            LoginActivity.loginUser.setRequests(requests);
                        }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void uploadProfileImage(Bitmap bitmap,String username){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/"+username+"_profilepic.jpg");
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }
    public void downloadProfileImage(String username, final AppCallBack callBack){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("images/"+username+"_profilepic.jpg");;

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                callBack.callback(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callBack.callback(null);
            }
        });
    }
    public void uploadMsgImage(byte[] bytes,String filename){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("chat_images/"+filename);
        UploadTask uploadTask = imagesRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }
    public void downloadMsgImage( String filename,final AppCallBack callBack){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("chat_images/"+filename);;

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    byte[] deBytes=EncryptionHelper.decryptImage(bytes);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(deBytes, 0, deBytes.length);
                    callBack.callback(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callBack.callback(null);
            }
        });
    }
    public void addFriend(String userName,Friend f){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", f.getFullname());
        map.put("token", f.getToken());
        databaseReference.child("users/"+userName+"/friends/").child(f.getUserName()).setValue(map);
        databaseReference.child("users/"+userName+"/requests/").child(f.getUserName()).removeValue();
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("fullname", LoginActivity.loginUser.getFullname());
        map2.put("token", FirebaseInstanceId.getInstance().getToken());
        databaseReference.child("users/"+f.getUserName()+"/friends/").child(userName).setValue(map2);
    }
    public void removeFriendRequest(String userName,String friendUserName){
        databaseReference.child("users/"+userName+"/requests/").child(friendUserName).removeValue();
    }
    public void searchUsers(final String search, final AppCallBack callBack){
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Friend> list=new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if((postSnapshot.getKey().contains(search) || postSnapshot.getKey().equals(search)) && !postSnapshot.getKey().equals(LoginActivity.loginUser.getUsername()) ) {
                         Friend f = new Friend(postSnapshot.getKey(), postSnapshot.child("fullname").getValue().toString(), postSnapshot.child("token").getValue().toString());
                      //  Friend f = new Friend(postSnapshot.getKey(),"", "");
                        list.add(f);

                    }
                }
                callBack.callback(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sendFriendRequest(String userName){
        HashMap<String, Object> map = new HashMap<>();
        map.put("fullname", LoginActivity.loginUser.getUsername());
        map.put("token", FirebaseInstanceId.getInstance().getToken());
        databaseReference.child("users/"+userName+"/requests/").child(LoginActivity.loginUser.getUsername()).setValue(map);
    }
}
