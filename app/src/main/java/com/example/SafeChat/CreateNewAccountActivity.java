package com.example.SafeChat;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateNewAccountActivity extends AppCompatActivity implements AppCallBack {

    EditText fullNameEditText;
    EditText userNameEditText;
    EditText passwordEditText;
    EditText rePasswordEditText;
    Button createNewAccBtn;
    TextView userNameErrorTxt;
    TextView fullNameErrorTxt;
    TextView passwordErrorTxt;
    TextView repasswordErrorTxt;
    CircleImageView  profileImageView;
    boolean fieldsError=false;
    Bitmap profileBitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        initUiComponents();
        setListeners();
    }
    void initUiComponents(){
        fullNameEditText=findViewById(R.id.fullNameEditText);
        userNameEditText=findViewById(R.id.userNameEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        rePasswordEditText=findViewById(R.id.rePasswordEditText);
        createNewAccBtn=findViewById(R.id.createNewAccBtn);
        userNameErrorTxt=findViewById(R.id.userNameErrorTxt);
        fullNameErrorTxt=findViewById(R.id.fullNameErrorTxt);
        passwordErrorTxt=findViewById(R.id.passwordErrorTxt);
        repasswordErrorTxt=findViewById(R.id.repasswordErrorTxt);
        profileImageView=findViewById(R.id.profileImageView);
    }
    void setListeners(){
        createNewAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldsError= false;
                restFields();
                if(!Pattern.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", fullNameEditText.getText().toString())){
                    fullNameEditText.setBackgroundResource(R.drawable.app_edittext_red);
                    fieldsError=true;
                    showError(fullNameErrorTxt,"invalid full name");
                }
                if(!Pattern.matches("^[a-z0-9_-]{3,16}$", userNameEditText.getText().toString())){
                    userNameEditText.setBackgroundResource(R.drawable.app_edittext_red);
                    fieldsError=true;
                    showError(userNameErrorTxt,"invalid username");
                }
                if(!Pattern.matches("^([a-zA-Z0-9@*#]{8,15})$", passwordEditText.getText().toString())){
                    passwordEditText.setBackgroundResource(R.drawable.app_edittext_red);
                    fieldsError=true;
                    showError(passwordErrorTxt,"invalid password");
                }
                if(!rePasswordEditText.getText().toString().equals(passwordEditText.getText().toString())){
                    rePasswordEditText.setBackgroundResource(R.drawable.app_edittext_red);
                    fieldsError=true;
                    showError(repasswordErrorTxt,"password dosn't match");
                }
                if(!fieldsError) {
                    FirebaseDbController controller= new FirebaseDbController();
                    if(profileBitmap!=null) {
                        controller.uploadProfileImage(profileBitmap,userNameEditText.getText().toString());
                    }
                    controller.addNewUser(new User(fullNameEditText.getText().toString(), userNameEditText.getText().toString(), passwordEditText.getText().toString()));
                    login(userNameEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userNameEditText.setBackgroundResource(R.drawable.app_edittext);
                hideError(userNameErrorTxt);
                fieldsError=false;
                new FirebaseDbController().checkUserName(s.toString(),CreateNewAccountActivity.this);
            }
        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                if (EasyPermissions.hasPermissions(CreateNewAccountActivity.this, galleryPermissions)) {
                    Intent intent=new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                    startActivityForResult(intent,1);
                } else {
                    EasyPermissions.requestPermissions(CreateNewAccountActivity.this, "Access for storage",
                            101, galleryPermissions);
                }

            }
        });

    }
    void showError(TextView textView,String error){
        textView.setVisibility(View.VISIBLE);
        textView.setText(error);
    }
    void hideError(TextView textView){
        textView.setVisibility(View.GONE);
        textView.setText("");
    }
    void  restFields(){
        fullNameEditText.setBackgroundResource(R.drawable.app_edittext);
        hideError(fullNameErrorTxt);
        userNameEditText.setBackgroundResource(R.drawable.app_edittext);
        hideError(userNameErrorTxt);
        passwordEditText.setBackgroundResource(R.drawable.app_edittext);
        hideError(passwordErrorTxt);
        rePasswordEditText.setBackgroundResource(R.drawable.app_edittext);
        hideError(repasswordErrorTxt);
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

    @Override
    public void callback(Object o) {
        boolean exists= (boolean) o;
        if(exists){
            userNameEditText.setBackgroundResource(R.drawable.app_edittext_red);
            showError(userNameErrorTxt,"user exists");
            fieldsError=true;
        }else {
            fieldsError=false;
        }
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
                    Bitmap bitmap= BitmapFactory.decodeFile(img);
                    profileBitmap=scaleBitmap(bitmap);
                    profileImageView.setImageBitmap(profileBitmap);
                    break;
            }
    }
    public void login(String userName,String pass){
        SharedPreferences sharedPref = getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username",userName );
        editor.putString("pass", pass);
        editor.commit();
        startActivity(new Intent(CreateNewAccountActivity.this,SplashActivity.class));
    }
}
