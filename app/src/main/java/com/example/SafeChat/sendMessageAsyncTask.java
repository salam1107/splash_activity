package com.example.SafeChat;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sendMessageAsyncTask extends AsyncTask<Void,Void,Void> {
    String msg;
    String token;
    String senderUserName;
    int type;
    public sendMessageAsyncTask(String msg, String token, String senderUserName, int type){
        this.msg=msg;
        this.token=token;
        this.senderUserName=senderUserName;
        this.type=type;
    }
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject json=new JSONObject();
            JSONObject dataJson=new JSONObject();
            dataJson.put("msg",msg);
            dataJson.put("sender",senderUserName);
            dataJson.put("type",type);
            json.put("data",dataJson);
            json.put("to",token);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            Request request = new Request.Builder()
                    .header("Authorization","key="+"AAAAXfTnN08:APA91bEDiTBX_ELCaR-0KASGPO9KKgw3R_uQNnAQxL69u7IShM0PU432br4WFCq6_0ONmqcwynGKcMgReBuV3iAKbBZTRR6j6RupkVUVvD-eVEY09saQfJvZtftDb8UBVYxYbKct5AYp")
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String finalResponse = response.body().string();
        }catch (Exception e){
            //Log.d(TAG,e+"");
        }
        return null;
    }
}
