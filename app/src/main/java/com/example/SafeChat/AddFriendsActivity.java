package com.example.SafeChat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.Friend;
import com.example.SafeChat.adapters.FriendsListAdapter;

import java.util.ArrayList;

public class AddFriendsActivity extends AppCompatActivity {
    EditText searchFriendsEditText;
    ListView friendsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initUiComponents();
        setListeners();
    }
    void initUiComponents(){
        searchFriendsEditText=findViewById(R.id.searchFriendsEditText);
        friendsListView=findViewById(R.id.friendsListView);
    }
    void setListeners(){

        searchFriendsEditText.setImeActionLabel("search", KeyEvent.KEYCODE_ENTER);
        searchFriendsEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        AppCallBack appCallBack=new AppCallBack() {
                            @Override
                            public void callback(Object o) {
                                ArrayList<Friend> list= (ArrayList<Friend>) o;
                                FriendsListAdapter friendsListAdapter=new FriendsListAdapter(AddFriendsActivity.this,list,true);
                                friendsListView.setAdapter(friendsListAdapter);
                            }
                        };
                        new FirebaseDbController().searchUsers(searchFriendsEditText.getText().toString(),appCallBack);
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });
    }
}
