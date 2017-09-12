package com.example.rutvik.chat;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener{

    public String nickNameEntered,countrySelected,stateSelected,emailEntered;
    Button map,save;
    EditText nickName,password,city,year,email;
    Spinner countrySpinner,stateSpinner;
    EditText lat, lon;
    int flag;
    ArrayList<String> countryArrayList = new ArrayList<String>();
    ArrayList<String> stateArrayList = new ArrayList<String>();
    String year1;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;

    public AddUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_user, container, false);
        map = (Button)view.findViewById(R.id.btnlonLat);
        map.setOnClickListener(this);
        save = (Button)view.findViewById(R.id.btnSave);
        save.setOnClickListener(this);
        nickName = (EditText)view.findViewById(R.id.etNn);
        nickName.requestFocus();
        nickName.setOnFocusChangeListener(this);
        email = (EditText)view.findViewById(R.id.etemail);
        email.setOnFocusChangeListener(this);
        password = (EditText)view.findViewById(R.id.etPass);
        password.setOnFocusChangeListener(this);
        city = (EditText)view.findViewById(R.id.etCity);
        city.setOnFocusChangeListener(this);
        year = (EditText)view.findViewById(R.id.etYear);
        year.setOnFocusChangeListener(this);
        countryArrayList.add("Select Country");
        stateArrayList.add("Select State");
        lat = (EditText) view.findViewById(R.id.latEt);
        lon = (EditText) view.findViewById(R.id.lonEt);
        getRequestCountry();
        auth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void getRequestCountry() {
        String url = "http://bismarck.sdsu.edu/hometown/countries";
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        countryArrayList.add(response.getString(i));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                countrySpinner = (Spinner) getActivity().findViewById(R.id.conSpinner);
                countrySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        countryArrayList));
                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                        try{
                            countrySelected=countryArrayList.get(position);
                            getRequestState();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }}
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(getContext()).add(getRequest);
    }

    public void getRequestState() {
        stateArrayList.clear();
        if (countrySelected == "Select Country") {
            stateArrayList.add("Select State");
            stateSpinner = (Spinner) getActivity().findViewById(R.id.stateSpinner);
            stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                    stateArrayList));
        } else {
            String url = "http://bismarck.sdsu.edu/hometown/states?country=" + countrySelected;
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    try {
                        stateArrayList.add("Select State");
                        for (int i = 0; i < response.length(); i++) {
                            stateArrayList.add(response.getString(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stateSpinner = (Spinner) getActivity().findViewById(R.id.stateSpinner);
                    stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                            stateArrayList));
                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try{
                                stateSelected=stateArrayList.get(position);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            };
            JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
            VolleyQueue.instance(getContext()).add(getRequest);
        }
    }

    public void saveDataBismarck() {
        JSONObject data = new JSONObject();
        if(lat.getText().toString().equals("")){
            try {
                data.put("nickname", nickName.getText().toString());
                data.put("password", password.getText().toString());
                data.put("country", countrySpinner.getSelectedItem().toString());
                data.put("state", stateSpinner.getSelectedItem().toString());
                data.put("city", city.getText().toString());
                data.put("year", Integer.valueOf(year.getText().toString()));
            } catch (Exception error) {
                error.getMessage();
                return;
            }}
        else{
            try {
                data.put("nickname", nickName.getText().toString());
                data.put("password", password.getText().toString());
                data.put("country", countrySpinner.getSelectedItem().toString());
                data.put("state", stateSpinner.getSelectedItem().toString());
                data.put("city", city.getText().toString());
                data.put("year", Integer.valueOf(year.getText().toString()));
                data.put("longitude",Double.valueOf(lon.getText().toString()));
                data.put("latitude",Double.valueOf(lat.getText().toString()));
            } catch (Exception error) {
                error.getMessage();
                return;
            }
        }
        String url = "http://bismarck.sdsu.edu/hometown/adduser";
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                clearItems(nickName);
                clearItems(password);
                clearItems(city);
                clearItems(year);
                clearItems(email);
                clearItems(lat);
                clearItems(lon);
                countrySpinner.setSelection(0);
                stateSpinner.setSelection(0);
                Toast.makeText(getActivity(), "Data saved successfully on the server!", Toast.LENGTH_LONG).show();
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Data not stored on bismarck server!", Toast.LENGTH_LONG).show();
            }
        };

        JsonObjectRequest postRequest = new JsonObjectRequest(url, data, success, failure);
        VolleyQueue.instance(getContext()).add(postRequest);
    }

    public void dupCheckNickName() {

        String url ="http://bismarck.sdsu.edu/hometown/nicknameexists?name="+nickNameEntered;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        if(response.startsWith("true")){
                            nickName.setError( "Nickname already taken! Please enter another Nickname!" );
                            flag=1;
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        });
        VolleyQueue.instance(getContext()).add(stringRequest);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnlonLat:
                Fragment mapFragment = new LatLonMap();
                Bundle bundle = new Bundle();
                bundle.putString("country", countrySpinner.getSelectedItem().toString());
                bundle.putString("state", stateSpinner.getSelectedItem().toString());
                bundle.putString("city", city.getText().toString());
                mapFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.hide(getFragmentManager().findFragmentByTag("AddUser"));
                fragmentTransaction.add(R.id.item_detail_container,mapFragment,"LatLonMap");
                fragmentTransaction.addToBackStack("LatLonMap");
                fragmentTransaction.commit();
                break;
            case R.id.btnSave:
                //save.requestFocusFromTouch();
                Toast.makeText(getActivity(), "Please wait data is being added to the server!", Toast.LENGTH_LONG).show();
                validation();
                break;
        }
    }

    public void saveDataFireBase() {

        String emailEntered = email.getText().toString().trim();
        String passwordEntered = password.getText().toString().trim();

        auth.createUserWithEmailAndPassword(emailEntered, passwordEntered)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Authentication failed!" + task.getException(), Toast.LENGTH_LONG).show();
                        } else {
                            addNickname(task.getResult().getUser());
                            saveDataBismarck();
                        }
                    }
                });
    }
    public void addNickname(FirebaseUser FbUser){
        String nickF = nickName.getText().toString().trim();
        WriteNickName(FbUser.getUid(),nickF, FbUser.getEmail());
    }

    public void WriteNickName(String uid, String nickname, String email){
        User user = new User(nickname,email);
        databaseReference =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-49416.firebaseio.com/users");
        databaseReference.child(uid).setValue(user);
        /*databaseReference1 =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-49416.firebaseio.com/chats/"+nickname+"/Message1");
        databaseReference1.child("Sender").setValue(nickname);
        databaseReference1.child("Message").setValue("hello");
        databaseReference1.child("TimeStamp").setValue("123");*/
    }

    public void validation(){
        year1 = year.getText().toString();
        if (nickName.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Nickname!", Toast.LENGTH_LONG).show();
            nickName.requestFocus();
        } else if (email.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Email!", Toast.LENGTH_LONG).show();
            email.requestFocus();
        } else if (password.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Password!", Toast.LENGTH_LONG).show();
            password.requestFocus();
        } else if (countrySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select Country!", Toast.LENGTH_LONG).show();
        } else if (stateSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select State!", Toast.LENGTH_LONG).show();
        } else if (city.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter City!", Toast.LENGTH_LONG).show();
            city.requestFocus();
        } else if (year.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Year!", Toast.LENGTH_LONG).show();
            year.requestFocus();
        }
        /*else if (lat.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please Select Latitude and Longitude!", Toast.LENGTH_LONG).show();
        }*/
        else if (!isValidEmail(email.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter valid Email!!", Toast.LENGTH_LONG).show();
        }
        else if (!city.getText().toString().matches("[a-zA-Z ]+")) {
            Toast.makeText(getActivity(), "Please enter valid City!", Toast.LENGTH_LONG).show();
        } else if (year1!=null) {
            int yearEntered = Integer.valueOf(year.getText().toString());
            if (yearEntered < 1970 || yearEntered > 2017) {
                year.setError("Please enter year between 1970 and 2017!");
            }
            else if(nickName.getError()== null && password.getError()== null && city.getError()== null
                    && year.getError()== null && isValidEmail(email.getText().toString())
                    && city.getText().toString().matches("[a-zA-Z ]+")){
                //Toast.makeText(getActivity(), "success!", Toast.LENGTH_LONG).show();
                saveDataFireBase();
            }
            else {
                Toast.makeText(getActivity(), "Please enter valid data!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean Focus) {
        switch(v.getId()){
            case R.id.etNn:
                if(!Focus){
                    if( nickName.getText().toString().length()==0 ){
                        nickName.setError( "Please enter Nickname!" );
                    }
                    else{
                        nickNameEntered=nickName.getText().toString();
                        dupCheckNickName();
                    }
                }
                break;
            case R.id.etemail:
                if(!Focus){
                    if( email.getText().toString().length()==0 ){
                        email.setError( "Please enter Email!" );
                    }
                    else{
                        emailEntered=email.getText().toString();
                        if(!isValidEmail(emailEntered)){
                            email.setError( "Please enter valid Email!" );
                        }
                    }
                }
                break;
            case R.id.etPass:
                if (!Focus) {
                    if ( password.getText().toString().length() == 0) {
                        password.setError("Please enter Password!");
                    }
                    else if (password.getText().toString().length() < 6){
                        password.setError("Please enter Password with atleast 6 characters!");
                    }
                }
                break;
            case R.id.etCity:
                if (!Focus) {
                    if (city.getText().toString().length()==0) {
                        city.setError("Please enter City!");
                    }
                    else {
                        String cityEntered=city.getText().toString();
                        if (!cityEntered.matches("[a-zA-Z ]+")){
                            city.setError("Please enter valid City!");
                        }
                    }
                }
                break;
            case R.id.etYear:
                if(!Focus){
                    if(year.getText().toString().length()==0)
                        year.setError("Please enter Year!");
                    if(year.getText().toString().length()>0) {
                        int yearEntered=Integer.valueOf(year.getText().toString());
                        if (yearEntered < 1970 || yearEntered > 2017)
                            year.setError("Please enter year between 1970 and 2017!");
                    }
                }
                break;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    void clearItems(View view){
        EditText editText = (EditText) view;
        editText.setText("");
    }

}
