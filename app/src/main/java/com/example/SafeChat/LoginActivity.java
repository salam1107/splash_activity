package com.example.SafeChat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.User;

public class LoginActivity extends AppCompatActivity {
    TextView CreateNewAccTxtBtn;
    Button loginBtn;
    EditText userNameEditText;
    EditText passwordEditText;
    public static User loginUser=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_loign);

        initUiComponents();
        setOnClickListeners();
    }
    void initUiComponents(){
        CreateNewAccTxtBtn=(TextView)findViewById(R.id.CreateNewAccTxtBtn);
        loginBtn=(Button)findViewById(R.id.loginBtn);
        userNameEditText=findViewById(R.id.userNameEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
    }
    void setOnClickListeners(){
        CreateNewAccTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateNewAccountActivity.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCallBack appCallBack=new AppCallBack() {
                    @Override
                    public void callback(Object o) {
                        User user= (User) o;
                        if (user == null) {
                            Toast.makeText(LoginActivity.this,"user not founded",Toast.LENGTH_LONG).show();
                        }else{
                            loginUser=user;
                            loginUser.setUsername(userNameEditText.getText().toString());
                            SharedPreferences sharedPref = getSharedPreferences("app",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", loginUser.getUsername());
                            editor.putString("pass", loginUser.getPassword());
                            editor.commit();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));

                        }
                    }
                };
                new FirebaseDbController().login(userNameEditText.getText().toString(),passwordEditText.getText().toString(),appCallBack);
            }
        });
    }
}
