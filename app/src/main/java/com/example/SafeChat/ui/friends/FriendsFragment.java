package com.example.SafeChat.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.SafeChat.AddFriendsActivity;
import com.example.SafeChat.AppObjects.AppCallBack;
import com.example.SafeChat.AppObjects.Friend;
import com.example.SafeChat.FirebaseDbController;
import com.example.SafeChat.LoginActivity;
import com.example.SafeChat.R;
import com.example.SafeChat.adapters.FriendsListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private FriendsViewModel dashboardViewModel;
    ListView friendsListView;
    FloatingActionButton floatingActionButton;
    EditText searchEditText;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(FriendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListView=root.findViewById(R.id.friendsListView);
        floatingActionButton=root.findViewById(R.id.floatingActionButton);
        searchEditText=root.findViewById(R.id.searchEditText);
        searchEditText.setImeActionLabel("search", KeyEvent.KEYCODE_ENTER);
        FriendsListAdapter chatsListAdapter=new FriendsListAdapter(getContext(), LoginActivity.loginUser.getFriends(),false);
        friendsListView.setAdapter(chatsListAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddFriendsActivity.class));
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        ArrayList<Friend> list=new ArrayList<>();
                        for(int i=0;i<LoginActivity.loginUser.getFriends().size();i++){
                            if(LoginActivity.loginUser.getFriends().get(i).getUserName().contains(searchEditText.getText()) || LoginActivity.loginUser.getFriends().get(i).getFullname().contains(searchEditText.getText())){
                                list.add(LoginActivity.loginUser.getFriends().get(i));
                            }
                        }
                        FriendsListAdapter chatsListAdapter=new FriendsListAdapter(getContext(),list,false);
                        friendsListView.setAdapter(chatsListAdapter);
                        return true;
                    }
                }
                return false;
            }
        });
        return root;
    }
}