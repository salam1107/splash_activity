package com.example.SafeChat.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.FirebaseDbController;
import com.example.SafeChat.LoginActivity;
import com.example.SafeChat.MainActivity;
import com.example.SafeChat.R;
import com.example.SafeChat.adapters.FriendRequestListAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;
    CircleImageView profileImageView;
    TextView userFullNameTxt;
    ListView requestsListView;
    Button  logoutBtn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView=root.findViewById(R.id.profileImageView);
        userFullNameTxt=root.findViewById(R.id.userFullNameTxt);
        requestsListView=root.findViewById(R.id.requestsListView);
        logoutBtn=root.findViewById(R.id.logoutBtn);
        FriendRequestListAdapter adapter=new FriendRequestListAdapter(getContext(),LoginActivity.loginUser.getRequests());
        requestsListView.setAdapter(adapter);
        setListeners();
        if(LoginActivity.loginUser!=null){
            userFullNameTxt.setText(LoginActivity.loginUser.getFullname());
            AppCallBack appCallBack=new AppCallBack() {
                @Override
                public void callback(Object o) {
                    Bitmap bitmap= (Bitmap) o;
                    if(bitmap!=null)
                        profileImageView.setImageBitmap(bitmap);
                }
            };
            new FirebaseDbController().downloadProfileImage(LoginActivity.loginUser.getUsername(),appCallBack);
        }
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("username","");
                editor.putString("pass", "");
                editor.commit();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        return root;
    }
    void setListeners(){
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(getContext(), galleryPermissions)) {
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    startActivityForResult(intent,1);
                } else {
                    EasyPermissions.requestPermissions(ProfileFragment.this, "Access for storage",
                            101, galleryPermissions);
                }
            }
        });
    }
    private Bitmap scaleBitmap(Bitmap bitmap) {
        int maxWidth=300;
        int maxHeight=300;
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
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 1:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String img = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap bitmap= BitmapFactory.decodeFile(img);
                    Bitmap profileBitmap=scaleBitmap(bitmap);
                    profileImageView.setImageBitmap(profileBitmap);
                    new FirebaseDbController().uploadProfileImage(profileBitmap,LoginActivity.loginUser.getUsername());
                    break;
            }
    }
}