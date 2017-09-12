package com.example.rutvik.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener,View.OnFocusChangeListener {

    EditText email, password;
    TextView error;
    Button login,reset;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        email = (EditText)view.findViewById(R.id.etEmail);
        email.requestFocus();
        email.setOnFocusChangeListener(this);
        password = (EditText)view.findViewById(R.id.etPass);
        password.setOnFocusChangeListener(this);
        error = (TextView) view.findViewById(R.id.error);
        login = (Button) view.findViewById(R.id.btnLogin);
        login.setOnClickListener(this);
        reset = (Button) view.findViewById(R.id.aReset);
        reset.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onFocusChange(View v, boolean Focus) {
        switch(v.getId()){
            case R.id.etEmail:
                if(!Focus){
                    if( email.getText().toString().length()==0 ){
                        //email.setError( "" );
                    }
                    else{
                        String emailEntered=email.getText().toString();
                        if(!isValidEmail(emailEntered)){
                            email.setError( "Please enter valid Email!" );
                        }
                    }
                }
                else{error.setText("");}
                break;
            case R.id.etPass:
                if (!Focus) {
                    if ( password.getText().toString().length() == 0) {
                        //password.setError("");
                    }
                    else if (password.getText().toString().length() < 6){
                        password.setError("Please enter Password with atleast 6 characters!");
                    }
                }
                break;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                Toast.makeText(getActivity(), "Logging in please wait!!", Toast.LENGTH_LONG).show();
                validation();
                break;
            case R.id.aReset:
                email.setText("");
                password.setText("");
                error.setText("");
                break;
        }
    }

    public void Login() {
        String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();

        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            error.setText("Wrong username or password!");
                        } else {
                            Intent intent = new Intent(getActivity(), MainViewActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                });
    }


    public void validation(){
        if (email.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Email!", Toast.LENGTH_LONG).show();
            email.requestFocus();
        } else if (password.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Password!", Toast.LENGTH_LONG).show();
            password.requestFocus();
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(getActivity(), "Please enter Password with atleast 6 characters!", Toast.LENGTH_LONG).show();
            password.requestFocus();
        } else if(password.getError()== null && password.getText().toString().length() >= 6 && isValidEmail(email.getText().toString())){
            Login();
        } else {
            Toast.makeText(getActivity(), "Please enter valid data!", Toast.LENGTH_LONG).show();
        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
