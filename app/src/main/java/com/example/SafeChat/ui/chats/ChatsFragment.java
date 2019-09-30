package com.example.SafeChat.ui.chats;

import android.os.Bundle;
import android.util.Log;
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

import com.example.SafeChat.AppObjects.Chat;
import com.example.SafeChat.AppObjects.ChatMessage;
import com.example.SafeChat.AppObjects.Friend;
import com.example.SafeChat.ChatActivity;
import com.example.SafeChat.LoginActivity;
import com.example.SafeChat.R;
import com.example.SafeChat.adapters.ChatMessagesListAdapter;
import com.example.SafeChat.adapters.ChatsListAdapter;
import com.example.SafeChat.adapters.FriendsListAdapter;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private ChatViewModel homeViewModel;
    ListView chatsListView;
    ArrayList<Chat> chats;
    EditText searchEditText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ChatViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chats, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
       /* homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        chatsListView=root.findViewById(R.id.chatsListView);
        searchEditText=root.findViewById(R.id.searchEditText);


        chats=new ArrayList<>();
        String[] arr=getContext().fileList();
        for(int i=0;i<arr.length;i++){
            String userName=arr[i].substring(0,arr[i].lastIndexOf('.'));
            Log.d("fileee",arr[i].substring(0,arr[i].lastIndexOf('.')));
            //LoginActivity.loginUser.getFriends().
            for(int j=0;j<LoginActivity.loginUser.getFriends().size();j++){
                if(LoginActivity.loginUser.getFriends().get(j).getUserName().equals(userName)){
                    Chat c=new Chat(userName,LoginActivity.loginUser.getFriends().get(j).getToken(),getLastMsgFromChat(arr[i]));
                    chats.add(c);
                }
            }
        }
        ChatsListAdapter chatsListAdapter=new ChatsListAdapter(getContext(),chats);
        chatsListView.setAdapter(chatsListAdapter);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        ArrayList<Chat> list=new ArrayList<>();
                        for(int i=0;i<chats.size();i++){
                            if(chats.get(i).getUserName().contains(searchEditText.getText())){
                                list.add(chats.get(i));
                            }
                        }
                        ChatsListAdapter chatsListAdapter=new ChatsListAdapter(getContext(),list);
                        chatsListView.setAdapter(chatsListAdapter);
                        return true;
                    }
                }
                return false;
            }
        });
        return root;
    }
    String getLastMsgFromChat(String fileName){
        String msg="";
        try {
            FileInputStream fi = getContext().openFileInput(fileName);
            ObjectInputStream oi = new ObjectInputStream(fi);
            ArrayList<ChatMessage> list = (ArrayList<ChatMessage>) oi.readObject();
            msg=list.get(list.size()-1).msg;
            if(list.get(list.size()-1).type==1)
                msg="send you a picture";
        }catch (Exception e){
            e.printStackTrace();
        }
        return  msg;

    }
}