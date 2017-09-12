package com.example.rutvik.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class ChatDetails extends Fragment implements View.OnClickListener {

    ScrollView sView;
    Button sButton;
    EditText chatArea;
    LinearLayout lLayout;
    TextView heading;
    String sender,receiver;
    Firebase ref1, ref2;

    public ChatDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_details, container, false);
        Bundle bundle = this.getArguments();

        sView = (ScrollView) view.findViewById(R.id.scrollView);
        sButton = (Button) view.findViewById(R.id.sButton);
        sButton.setOnClickListener(this);
        chatArea = (EditText) view.findViewById(R.id.chatArea);
        heading = (TextView) view.findViewById(R.id.chatHeading);
        heading.setText("Chat with " + bundle.getString("receiver"));
        lLayout = (LinearLayout) view.findViewById(R.id.layout1);
        sender = bundle.getString("sender");
        receiver = bundle.getString("receiver");
        Firebase.setAndroidContext(getContext());
        ref1 = new Firebase("https://chat-49416.firebaseio.com/chats/"+sender+"_"+receiver);
        ref2 = new Firebase("https://chat-49416.firebaseio.com/chats/"+receiver+"_"+sender);


        ref1.addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("Text").toString();
                String nickName = map.get("nickname").toString();

                if (nickName.equals(sender)) {
                    textBox("You:\n" + message, 1);
                } else {
                    textBox(receiver + ":\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sButton:
                String message = chatArea.getText().toString();
                if (!message.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("Text", message);
                    map.put("nickname", sender);
                    ref1.push().setValue(map);
                    ref2.push().setValue(map);
                    chatArea.setText("");
                }
                break;
        }
    }

    public void textBox(String message, int type){
        try{
            TextView textView = new TextView(getContext());
            textView.setText(message);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 10);
            textView.setLayoutParams(lp);

            if(type == 1) {
                textView.setBackgroundResource(R.drawable.textbox_sender);
            }
            else{
                textView.setBackgroundResource(R.drawable.textbox_receiver);
            }

            lLayout.addView(textView);
            sView.fullScroll(View.FOCUS_DOWN);
        }
        catch(Exception e){}
    }

}
