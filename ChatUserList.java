package com.example.rutvik.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatUserList extends Fragment implements ValueEventListener, AdapterView.OnItemClickListener {

    TextView noUsersText;
    ListView userList;
    String nickName;
    int total = 0;
    //ArrayAdapter<String> adapter;
    ArrayList<String> users=new ArrayList<String>();

    public ChatUserList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_user_list, container, false);
        Bundle bundle=this.getArguments();
        nickName = bundle.getString("nickname");
        noUsersText = (TextView) view.findViewById(R.id.noUsersText);
        userList = (ListView) view.findViewById(R.id.chatList);
        userList.setOnItemClickListener(this);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-49416.firebaseio.com/");
        DatabaseReference userList = databaseRef.child("chats");
        Query query = userList.orderByKey();
        query.addValueEventListener(this);
        users =new ArrayList<String>();
        return view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
            String  str= snapShot.getKey().toString();
            String[] separated = str.split("_");
            String send = separated[0];
            String receive = separated[1];
            if(send.equals(nickName)){
                users.add(receive);
            }
        }
        try{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, users);
            userList.setAdapter(adapter);}
        catch(Exception e){}

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putString("sender", nickName);
        bundle.putString("receiver", (String)userList.getItemAtPosition(position));
        ChatUserList chatUserList = new ChatUserList();
        ChatDetails fragment = new ChatDetails();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container1, fragment)
                .commit();
    }
}
