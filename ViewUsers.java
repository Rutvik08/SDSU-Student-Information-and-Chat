package com.example.rutvik.chat;


import android.app.FragmentManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewUsers extends Fragment implements TextWatcher, AbsListView.OnScrollListener, AdapterView.OnItemClickListener{

    public String nName,countrySelected,stateSelected,emailEntered;
    ArrayList<User> user;
    ListView listView;
    boolean loading;
    ListViewAdapter adapter;
    ListView mapView;
    Button viewUsers,chat,logout;
    EditText nickName,password,city,year,email;
    Spinner countrySpinner,stateSpinner;
    EditText lat, Lon;
    ArrayList<String> countryArrayList = new ArrayList<String>();
    ArrayList<String> stateArrayList = new ArrayList<String>();
    String year1;
    FirebaseAuth.AuthStateListener authListener;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    SQLiteDatabase db;
    SQLiteDao dao = new SQLiteDao();

    public ViewUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_users, container, false);
        Bundle bundle=this.getArguments();
        nName = bundle.getString("nickname");
        countryArrayList.add("Select Country");
        stateArrayList.add("Select State");
        listView=(ListView)view.findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        year = (EditText)view.findViewById(R.id.etYear);
        year.addTextChangedListener(this);
        countrySpinner = (Spinner) view.findViewById(R.id.conSpinner);
        stateSpinner = (Spinner) view.findViewById(R.id.stateSpinner);
        Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
        getRequestCountry();
        SQLiteUpload();
        // Inflate the layout for this fragment
        return view;
    }
    public void SQLiteUpload(){
        db=getContext().openOrCreateDatabase("Users", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + "USERS" + " ("
                + "nick_name" + " TEXT,"
                + "city" + " TEXT,"
                + "longitude" + " TEXT,"
                + "state" + " TEXT,"
                + "year" + " INTEGER,"
                + "id" + " INTEGER,"
                + "latitude" + " TEXT,"
                + "timestamp" + " TEXT,"
                + "country" + " TEXT"
                + ");");
        dao.setListView(listView);
        dao.getLastIdFromServer();
        dao.getLastIdFromDatabase(db);
        dao.setUsersFromDatabase(getContext(),db);
        dao.getFirstIdFromDatabase(db);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
        {
            Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
            if(loading == false)
            {
                loading = true;
            }
        }
    }

    public void getData(String country, String state) {
        String url;
        int yearFinal;
        String yearSubFinal=year.getText().toString();
        if (yearSubFinal.equals("")){
            yearFinal = 0;
        }
        else{
            yearFinal = Integer.valueOf(year.getText().toString());
        }
        if (country=="Select Country"){
            url = "http://bismarck.sdsu.edu/hometown/users";
            if (yearFinal != 0) {
                url = "http://bismarck.sdsu.edu/hometown/users?year=" + yearFinal;
            }
        }else {
            url = "http://bismarck.sdsu.edu/hometown/users?country=" + country;

            if (state != "Select State") {
                try {
                    String encoded = URLEncoder.encode(state, "UTF-8");
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + country + "&state=" + encoded;
                    if (yearFinal != 0) {
                        url = "http://bismarck.sdsu.edu/hometown/users?country=" + country + "&state=" + encoded + "&year=" + yearFinal;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                if (yearFinal != 0) {
                    url = "http://bismarck.sdsu.edu/hometown/users?country=" + country + "&year=" + yearFinal;
                }
            }
        }
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                user = new ArrayList<User>();
                adapter = new ListViewAdapter(user, getContext());
                listView.setAdapter(adapter);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        user.add(new User(jsonObject.getString("nickname"), jsonObject.getString("country"),
                                jsonObject.getString("state"), jsonObject.getString("city"), jsonObject.getInt("year")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest(url, success, failure);
        VolleyQueue.instance(getContext()).add(getRequest);
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

                countrySpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                        countryArrayList));
                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                        try{
                            countrySelected=countryArrayList.get(position);
                            dao.setCountry(countrySelected);
                            if(countrySelected=="Select Country"){
                                getRequestState();
                                SQLiteUpload();
                            }else {
                                getRequestState();
                                getData(countrySelected,null);
                            }
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
            stateSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,
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
                    stateSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                            stateArrayList));
                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            try{
                                stateSelected=stateArrayList.get(position);
                                dao.setState(stateSelected);
                                getData(countrySelected,stateSelected);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        year1 = year.getText().toString();
        dao.setYear(year1);
        if (year1.equals("")) {
            if(countrySelected=="Select Country"){
                SQLiteUpload();
            }else {
                getData(countrySelected,stateSelected);
            }
        }
        else{
            int yearEntered = Integer.valueOf(year.getText().toString());
            if (yearEntered < 1970 || yearEntered > 2017) {
                year.setError("Please enter year between 1970 and 2017!");
            }
            else {
                getData(countrySelected,stateSelected);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView text = (TextView) view.findViewById(R.id.nickNameL);
        Bundle bundle = new Bundle();
        bundle.putString("sender", nName);
        bundle.putString("receiver", text.getText().toString());
        ChatDetails fragment = new ChatDetails();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container1, fragment)
                .commit();
    }
}
