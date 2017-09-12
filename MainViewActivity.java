package com.example.rutvik.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainViewActivity extends AppCompatActivity implements View.OnClickListener,ValueEventListener{

    Button viewUsers,viewUsersMap,chat,logout;
    TextView userHeading, userHeading1;
    FirebaseAuth.AuthStateListener authListener;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    public String email,str, nickName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainViewActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        email = user.getEmail();
        userHeading = (TextView)findViewById(R.id.userHeading);
        userHeading1 = (TextView)findViewById(R.id.userHeading1);
        viewUsers = (Button) findViewById(R.id.viewUsers);
        viewUsers.setOnClickListener(this);
        viewUsersMap = (Button) findViewById(R.id.viewUsersMap);
        viewUsersMap.setOnClickListener(this);
        chat = (Button) findViewById(R.id.chat);
        chat.setOnClickListener(this);
        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(this);
        databaseReference =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-49416.firebaseio.com/users");
        Query query = databaseReference.orderByKey();
        query.addValueEventListener(this);
        //viewUsers.performClick();
        /*ViewUsers viewUsers = new ViewUsers();
        Bundle bundle=new Bundle();
        bundle.putString("email",email);
        bundle.putString("nickname",str);
        Toast.makeText(this,str, Toast.LENGTH_LONG).show();
        viewUsers.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container1, viewUsers)
                .commit();*/

    }

    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat:
                    ChatUserList chatUserList = new ChatUserList();
                    Bundle bundle=new Bundle();
                    bundle.putString("email",email);
                    bundle.putString("nickname",str);
                    chatUserList.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container1, chatUserList, "chatUserList")
                            .commit();
                    break;
                case R.id.viewUsers:
                    ViewUsers viewUsers = new ViewUsers();
                    Bundle bundle1=new Bundle();
                    bundle1.putString("email",email);
                    bundle1.putString("nickname",str);
                    viewUsers.setArguments(bundle1);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container1, viewUsers, "viewUsers")
                            .commit();
                    break;
                case R.id.viewUsersMap:
                    MapFragment viewUsersMap = new MapFragment();
                    Bundle bundle2=new Bundle();
                    bundle2.putString("email",email);
                    bundle2.putString("nickname",str);
                    viewUsersMap.setArguments(bundle2);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container1, viewUsersMap, "viewUsersMap")
                            .commit();
                    break;
                case R.id.logout:
                    auth.signOut();
                    break;
            }
        }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
            auth.signOut();
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Details details = new Details();
        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
            details = snapShot.getValue(Details.class);
            if(details.email.equals(email)){
                str = details.nickname;
            }
            }
        userHeading.setText("Welcome "+str+"!");
        userHeading1.setText("Welcome "+str+"!");
            details.setEmail(email);
        details.setNickname(str);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}


