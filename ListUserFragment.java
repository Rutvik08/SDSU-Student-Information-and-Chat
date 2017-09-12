package com.example.rutvik.chat;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListUserFragment extends Fragment  implements  AbsListView.OnScrollListener{

    ArrayList<User> user;
    ListView listView;
    boolean flag_loading;
    ListViewAdapter adapter;
    EditText etYear;
    Intent intent;
    String country,state,year;
    Button resetFilter,cancel,goToMap;
    Spinner countrySpinner,stateSpinner;
    ArrayList<String> countryArrayList = new ArrayList<String>();
    ArrayList<String> stateArrayList = new ArrayList<String>();
    public String countrySelected,stateSelected,year1;
    SQLiteDatabase db;
    int queryCount = 0;
    SQLiteDao dao = new SQLiteDao();
    public ListUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_list, container, false);
        listView=(ListView)view.findViewById(R.id.list);

        Bundle bundle=this.getArguments();
        country = bundle.getString("country");
        state = bundle.getString("state");
        year = bundle.getString("year");

        //getData(country,state);
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
            if(flag_loading == false)
            {
                flag_loading = true;
            }
        }

    }

    public void getData(String country, String state) {
        String url;
        int yearFinal;
        String yearSubFinal=year;
        if (yearSubFinal.equals("")){
            yearFinal = 0;
        }
        else{
            yearFinal = Integer.valueOf(year);
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
}
