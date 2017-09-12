package com.example.rutvik.chat;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements TextWatcher, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    public String nickNameEntered,emailEntered, addressText;
    ArrayList<User> user;
    private MapView mapView;
    private GoogleMap gMap;
    double latitude, longitude,lat,lng;
    EditText year;
    Spinner countrySpinner,stateSpinner;
    static int lastDatabase;
    static int lastServer;
    static int firstDatabase;
    Button more;
    int length;
    int yearEntered;
    SQLiteDatabase db;
    int count = 0;
    SQLiteDao sqLiteDao = new SQLiteDao();
    public String countrySelected,stateSelected,year1,nickname,yearFetched;
    ArrayList<String> countryArrayList = new ArrayList<String>();
    ArrayList<String> stateArrayList = new ArrayList<String>();
    List<Address> address;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view =inflater.inflate(R.layout.fragment_map, container, false);
        Bundle bundle=this.getArguments();
        more = (Button)view.findViewById(R.id.btnMore);
        nickNameEntered = bundle.getString("nickname");
        countryArrayList.add("Select Country");
        stateArrayList.add("Select State");
        year = (EditText)view.findViewById(R.id.etYear);
        year.addTextChangedListener(this);
        countrySpinner = (Spinner) view.findViewById(R.id.conSpinner);
        stateSpinner = (Spinner) view.findViewById(R.id.stateSpinner);
        more.setOnClickListener(this);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        getRequestCountry();
        SQLiteUpload();
        Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
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
        getUsersForMap();
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
                        Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
                        try{
                            countrySelected=countryArrayList.get(position);
                            if(countrySelected=="Select Country"){
                                getUsersForMap();
                                getRequestState();
                            }else {
                                getRequestState();
                                displayLocation(countrySelected, "Select State");
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
                                Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
                                stateSelected=stateArrayList.get(position);
                                getData(countrySelected,stateSelected);
                                displayLocation(countrySelected, stateSelected);
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

    public void getData(String country, String state) {
        removeMarkers();
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
                String country, city, state;
                double longitude, latitude;
                int year;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        nickname = jsonObject.getString("nickname");
                        country = jsonObject.getString("country");
                        state = jsonObject.getString("state");
                        city = jsonObject.getString("city");
                        year = jsonObject.getInt("year");
                        longitude = jsonObject.getDouble("longitude");
                        latitude = jsonObject.getDouble("latitude");
                        if (latitude == 0.0d && longitude == 0.0d) {
                            addressText = city + ", " + state + ", " + country;
                            Geocoder locator = new Geocoder(getContext());
                            try {
                                address = locator.getFromLocationName(addressText, 1);
                                for (Address addressLocation : address) {
                                    if (addressLocation.hasLatitude()) {
                                        lat = addressLocation.getLatitude();
                                    }
                                    if (addressLocation.hasLongitude()) {
                                        lng = addressLocation.getLongitude();
                                    }
                                }
                            } catch (Exception error) {
                                error.getMessage();
                            }
                        }
                        user.add(new User(nickname, country, state, city, year, latitude, longitude));

                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < user.size(); i++) {
                    nickname = user.get(i).getNickname();
                    displayMarker(user.get(i).getLatitude(), user.get(i).getLongitude());
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

    public void serverLastId(){
        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        Response.Listener<String> success = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        lastServer = Integer.parseInt(response);
                        lastServer--;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
        StringRequest getRequest = new StringRequest(url, success, failure);
        VolleyQueue.instance(getContext()).add(getRequest);
    }

    public void databaseFirstId(SQLiteDatabase sqLiteDatabase){
        db = sqLiteDatabase;
        Cursor mCursor=db.rawQuery("SELECT id FROM USERS ORDER BY ID ASC LIMIT 1;",null);
        if (mCursor.moveToFirst()){
            firstDatabase = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    public void mapDatabaseLastId(SQLiteDatabase sqLiteDatabase){
        db = sqLiteDatabase;
        Cursor mCursor=db.rawQuery("SELECT id FROM USERS ORDER BY ID DESC LIMIT 1;",null);
        if (mCursor.moveToFirst()){
            lastDatabase = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
    }

    public void moreData(SQLiteDatabase database){
        db = database;
        String url;
        databaseFirstId(db);
        url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&beforeid="+firstDatabase;
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (response != null) {
                    user = new ArrayList<User>();
                    for (int i = 0; i< 30; i++) {
                        String latitude;
                        String nickname;
                        String longitude;
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            ContentValues contentValues = new ContentValues(1);
                            nickname = jsonObject.getString("nickname");
                            contentValues.put("nick_name", nickname);
                            contentValues.put("city", jsonObject.getString("city"));
                            contentValues.put("state", jsonObject.getString("state"));
                            contentValues.put("country", jsonObject.getString("country"));
                            longitude = jsonObject.getString("longitude");
                            latitude = jsonObject.getString("latitude");
                            contentValues.put("year", jsonObject.getString("year"));
                            contentValues.put("id", jsonObject.getString("id"));
                            contentValues.put("timestamp", jsonObject.getString("time-stamp"));
                            if (latitude.equals("0.0") && longitude == ("0.0")) {
                                String add = "" + jsonObject.getString("city") + ", " + jsonObject.getString("state") + ", " + jsonObject.getString("country");
                                Geocoder locator = new Geocoder(getContext());
                                try {
                                    List<Address> address =
                                            locator.getFromLocationName(add, 1);
                                    for (Address addressLocation : address) {
                                        if (addressLocation.hasLatitude())
                                            latitude = String.valueOf(addressLocation.getLatitude());
                                        if (addressLocation.hasLongitude())
                                            longitude = String.valueOf(addressLocation.getLongitude());
                                    }
                                } catch (Exception error) {
                                }
                            }
                            contentValues.put("longitude", longitude);
                            contentValues.put("latitude", latitude);
                            db.insert("USERS", null, contentValues);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                getUsersForMap();
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(getContext()).add(getRequest);
    }

    public void mapDatabaseAllUsers(){
        user.clear();
        removeMarkers();
        Cursor mCursor = db.query(true, "USERS", new String[] {
                                "nick_name",
                                "longitude",
                                "latitude",
                                "timestamp",
                                "city",
                                "state",
                                "country",
                                "year",
                                "id",},null,
                        null,
                        null, null, "id" , null);
        if (mCursor.moveToFirst()) {
            do {
                User users = new User();
                users.setNickname(mCursor.getString(mCursor.getColumnIndexOrThrow("nick_name")));
                users.setYear(Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow("year"))));
                users.setLongitude(Double.parseDouble(mCursor.getString(mCursor.getColumnIndexOrThrow("longitude"))));
                users.setLatitude(Double.parseDouble(mCursor.getString(mCursor.getColumnIndexOrThrow("latitude"))));
                user.add(users);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        for (int i = 0; i < user.size() ; i++) {
            nickname = user.get(i).getNickname();
            yearEntered = user.get(i).getYear();
            displayMarker(user.get(i).getLatitude(), user.get(i).getLongitude());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        year1 = year.getText().toString();
        if (year1.equals("")) {
            Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
            if(countrySelected=="Select Country"){
                removeMarkers();
                getUsersForMap();
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
                Toast.makeText(getActivity(), "Data Loading! Please Wait!", Toast.LENGTH_LONG).show();
                getData(countrySelected,stateSelected);
            }
        }
    }

    public  void getUsersForMap(){
        String url;
        serverLastId();
        databaseFirstId(db);
        mapDatabaseLastId(db);
        url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid="+lastDatabase;
        Log.e("nan",url);
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (response != null) {
                    user = new ArrayList<User>();
                    if(response.length()>100)
                        length = 100;
                    else
                        length = response.length();
                    for (int i = 0; i< length; i++) {
                        String latitude;
                        String nickname;
                        String longitude;
                        String state;
                        String country;
                        String city;
                        String add;
                        try {
                            JSONObject jsonObject1 = response.getJSONObject(i);
                            ContentValues contentValues1 = new ContentValues(1);
                            nickname = jsonObject1.getString("nickname");
                            contentValues1.put("nick_name", nickname);
                            city = jsonObject1.getString("city");
                            state = jsonObject1.getString("state");
                            country = jsonObject1.getString("country");
                            contentValues1.put("city",city );
                            contentValues1.put("state", state);
                            contentValues1.put("country", country);
                            longitude = jsonObject1.getString("longitude");
                            latitude = jsonObject1.getString("latitude");
                            contentValues1.put("year", jsonObject1.getString("year"));
                            contentValues1.put("id", jsonObject1.getString("id"));
                            if (latitude.equals("0.0") && longitude == ("0.0")) {
                                add = "" + city + ", " + state  + ", " + country;
                                Geocoder locator = new Geocoder(getContext());
                                try {
                                    List<Address> address =
                                            locator.getFromLocationName(add, 1);
                                    for (Address addressLocation : address) {
                                        if (addressLocation.hasLatitude())
                                            latitude = String.valueOf(addressLocation.getLatitude());
                                        if (addressLocation.hasLongitude())
                                            longitude = String.valueOf(addressLocation.getLongitude());
                                    }
                                } catch (Exception error) {
                                }
                            }
                            contentValues1.put("longitude", longitude);
                            contentValues1.put("latitude", latitude);
                            Log.e("rew",jsonObject1.getString("nickname"));
                            db.insert("USERS", null, contentValues1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mapDatabaseAllUsers();
                    databaseFirstId(db);
                }
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
           }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(getContext()).add(getRequest);
    }

    public void removeMarkers(){
        if(gMap!=null){
            gMap.clear();
        }else{
        }
    }

    public void displayMarker(Double lat, Double lng) {
        LatLng location = new LatLng(lat, lng);
        gMap.addMarker(new MarkerOptions().position(location).title(nickname));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        moreData(db);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setRotateGesturesEnabled(false);
        gMap.getUiSettings().setScrollGesturesEnabled(true);
        gMap.getUiSettings().setTiltGesturesEnabled(false);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setAllGesturesEnabled(true);
        gMap.setOnInfoWindowClickListener(this);
    }

    public void displayLocation(String country, String state){
        Geocoder locator = new Geocoder(getContext());
        if(state.equals("Select State")){
        try {
            List<Address> address =
                    locator.getFromLocationName(country, 1);
            for (Address addressLocation : address) {
                if (addressLocation.hasLatitude())
                    latitude = addressLocation.getLatitude();
                if (addressLocation.hasLongitude())
                    longitude = addressLocation.getLongitude();
            }
        } catch (Exception error) {
        }
        LatLng display = new LatLng(latitude,longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(display));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(2));
    }
    else{
            try {
                List<Address> address =
                        locator.getFromLocationName(state+", "+country, 1);
                for (Address addressLocation : address) {
                    if (addressLocation.hasLatitude())
                        latitude = addressLocation.getLatitude();
                    if (addressLocation.hasLongitude())
                        longitude = addressLocation.getLongitude();
                }
            } catch (Exception error) {
            }
            LatLng display = new LatLng(latitude,longitude);
            gMap.moveCamera(CameraUpdateFactory.newLatLng(display));
            gMap.animateCamera(CameraUpdateFactory.zoomTo(6));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Bundle bundle = new Bundle();
        bundle.putString("sender", nickNameEntered);
        bundle.putString("receiver", marker.getTitle().toString());
        ChatDetails fragment = new ChatDetails();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container1, fragment)
                .commit();

    }
}
