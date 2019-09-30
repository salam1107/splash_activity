package com.example.SafeChat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.User;

//appsafechat@gmail.com chat5656
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getSharedPreferences("app",Context.MODE_PRIVATE);
                final String username  = sharedPref.getString("username","");
                String pass  = sharedPref.getString("pass","");
                Log.d("usernameee",username);
                if(username.equals("") || pass.equals("")){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    return;
                }
                AppCallBack appCallBack=new AppCallBack() {
                    @Override
                    public void callback(Object o) {
                        User user= (User) o;
                        if (user == null) {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }else{
                            LoginActivity.loginUser=user;
                            LoginActivity.loginUser.setUsername(username);
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));

                        }
                    }
                };
                new FirebaseDbController().login(username,pass,appCallBack);
            }
        }, 1500);
    }
}
