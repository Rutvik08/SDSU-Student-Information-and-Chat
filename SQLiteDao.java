package com.example.rutvik.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;



public class SQLiteDao {
    ArrayList<User> userDao;
    int reslen;
    static SQLiteDatabase db;
    int queryCount = 0;
    ListView listView;
    static String stateString = "Select State";
    static String countryString = "Select Country";
    static String yearString = "";
    static Context context;
    static int lastIdDtabase;
    static int lastIdServer;
    static int firstIdDatabase;
    int scrollPosition;

    ArrayList<User> reverse = new ArrayList<>();


    public void setCountry(String country){
        countryString = country;
    }
    public void setState(String state){
        stateString = state;
    } public void setYear(String year){
        yearString =year;
    }


    public void getMore100FromUsers(SQLiteDatabase database){
        db = database;
        String url;
        url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&beforeid="+firstIdDatabase;
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (response != null) {
                    userDao = new ArrayList<User>();
                    for (int i = 0; i< 30; i++) {
                        String latitude;
                        String nickname;
                        String longitude;
                        try {
                            //Log.i("rew", response.getString(i));
                            JSONObject user_data = response.getJSONObject(i);
                            //userDao.add(new User(user_data.getString("nickname"), user_data.getString("country"), user_data.getString("state"), user_data.getString("city"), user_data.getInt("year")));
                            ContentValues newName = new ContentValues(1);
                            nickname = user_data.getString("nickname");
                            newName.put("nick_name", nickname);
                            newName.put("city", user_data.getString("city"));
                            newName.put("state", user_data.getString("state"));
                            newName.put("country", user_data.getString("country"));
                            longitude = user_data.getString("longitude");
                            latitude = user_data.getString("latitude");
                            newName.put("year", user_data.getString("year"));
                            newName.put("id", user_data.getString("id"));
                            newName.put("timestamp", user_data.getString("time-stamp"));
                            if (latitude.equals("0.0") && longitude == ("0.0")) {
                                Log.e("rew", "-------------------- latitude will changed" + nickname);
                                String add = "" + user_data.getString("city") + ", " + user_data.getString("state") + ", " + user_data.getString("country");
                                Geocoder locator = new Geocoder(context);
                                try {
                                    List<Address> address =
                                            locator.getFromLocationName(add, 1);
                                    for (Address addressLocation : address) {
                                        Log.e("rew", "-------------------- latitude changed of user: " + nickname);
                                        if (addressLocation.hasLatitude())
                                            latitude = String.valueOf(addressLocation.getLatitude());
                                        if (addressLocation.hasLongitude())
                                            longitude = String.valueOf(addressLocation.getLongitude());
                                    }
                                } catch (Exception error) {
                                }
                            }
                            newName.put("longitude", longitude);
                            newName.put("latitude", latitude);
                            db.insert("USERS", null, newName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //getAllUsersFromDatabase();
                    getLastIdFromDatabase(db);
                    getLastIdFromServer();
                    getFirstIdFromDatabase(db);
                        /*CustomAdapter adapter = new CustomAdapter(userDao, getContext());
                        listView.setAdapter(adapter);*/
                }
                setUsersFromDatabase(context,db);
                // getAllUsersFromDatabase();
            }


        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
                //Log.i("Response","nickname"+ ANickname+"pass"+APassword+"Country"+ACountryName+"State"+AState+"city"+ACity+"year"+AYear);
            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(context).add(getRequest);
    }

    public void getLastIdFromDatabase(SQLiteDatabase sqLiteDatabase){
        db = sqLiteDatabase;
//        Cursor c = db.rawQuery("SELECT MAX(id) AS MAX FROM USERS;",null);
        Cursor mCursor=db.rawQuery("SELECT id FROM USERS ORDER BY ID DESC LIMIT 1;",null);

        if (mCursor.moveToFirst()){


            lastIdDtabase = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
            // do what ever you want here

        }


        Log.e("rew", "----------------------------------------last detail-"+lastIdDtabase);

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

    }
    /* public int mapDatabaseLastId(SQLiteDatabase sqLiteDatabase){
         db = sqLiteDatabase;
 //        Cursor c = db.rawQuery("SELECT MAX(id) AS MAX FROM USERS;",null);
         Cursor mCursor=db.rawQuery("SELECT id FROM USERS ORDER BY ID DESC LIMIT 1;",null);

         if (mCursor.moveToFirst()){


             lastIdDtabase = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
             // do what ever you want here

         }


         Log.e("rew", "----------------------------------------last detail-"+lastIdDtabase);

         if (mCursor != null && !mCursor.isClosed()) {
             mCursor.close();
         }

         return lastIdDtabase;

     }
 */
    public void getFirstIdFromDatabase(SQLiteDatabase sqLiteDatabase){
        db = sqLiteDatabase;
//        Cursor c = db.rawQuery("SELECT MAX(id) AS MAX FROM USERS;",null);
        Cursor mCursor=db.rawQuery("SELECT id FROM USERS ORDER BY ID ASC LIMIT 1;",null);

        if (mCursor.moveToFirst()){


            firstIdDatabase = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")));
            // do what ever you want here

        }


        Log.e("rew", "----------------------------------------first from database-"+firstIdDatabase);

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }

    }

    public void getLastIdFromServer(){


        String url = "http://bismarck.sdsu.edu/hometown/nextid";
        Response.Listener<String> success = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {




                    try {
                        //Log.i("rew", response.getString(i));


                        lastIdServer = Integer.parseInt(response);
                        lastIdServer--;
                        Log.e("rew", "jjjjjjjjjjjjjjjjjjjjjjjjj-------------------------- last server" + lastIdServer);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

        };


        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
                //Log.i("Response","nickname"+ ANickname+"pass"+APassword+"Country"+ACountryName+"State"+AState+"city"+ACity+"year"+AYear);

            }
        };

        // JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        Log.e("che",url);
        StringRequest getRequest = new StringRequest(url, success, failure);
        VolleyQueue.instance(context).add(getRequest);
    }


    public void deleteAllFromDatabase(SQLiteDatabase sqLiteDatabase){
        db = sqLiteDatabase;
        db.delete("Users",null,null);
        Log.e("rew","-------------------------------------- Table deleted");
    }

    public  void setUsersFromDatabase(final Context context1, SQLiteDatabase d) {
        //   userDao.clear();
        db = d;
        String url;
        context = context1;
        getLastIdFromDatabase(db);
        getLastIdFromServer();
        getFirstIdFromDatabase(db);
        url = "http://bismarck.sdsu.edu/hometown/users?reverse=true&afterid="+lastIdDtabase;
        Log.e("nan",url);
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                if (response != null) {
                    userDao = new ArrayList<User>();
                    if(response.length()>100)
                        reslen = 100;
                    else
                        reslen = response.length();
                    queryCount++;
                    Log.e("rew", "---------QueryCount" + queryCount);
                    for (int i = 0; i< reslen; i++) {
                        String latitude;
                        String nickname;
                        String longitude;
                        String state;
                        String country;
                        String city;
                        String add;



                        try {
                            JSONObject user_data = response.getJSONObject(i);
                            ContentValues newName = new ContentValues(1);
                            nickname = user_data.getString("nickname");
                            newName.put("nick_name", nickname);
                            city = user_data.getString("city");
                            state = user_data.getString("state");
                            country = user_data.getString("country");
                            newName.put("city",city );
                            newName.put("state", state);
                            newName.put("country", country);
                            longitude = user_data.getString("longitude");
                            latitude = user_data.getString("latitude");
                            newName.put("year", user_data.getString("year"));
                            newName.put("id", user_data.getString("id"));
                            //newName.put("timestamp", user_data.getString("time-stamp"));
                            if (latitude.equals("0.0") && longitude == ("0.0")) {
                                Log.e("rew", "-------------------- latitude will changed" + nickname);
                                add = "" + city + ", " + state  + ", " + country;
                                Geocoder locator = new Geocoder(context);
                                try {
                                    List<Address> address =
                                            locator.getFromLocationName(add, 1);
                                    for (Address addressLocation : address) {
                                        Log.e("rew", "-------------------- latitude changed of user: " + nickname);
                                        if (addressLocation.hasLatitude())
                                            latitude = String.valueOf(addressLocation.getLatitude());
                                        if (addressLocation.hasLongitude())
                                            longitude = String.valueOf(addressLocation.getLongitude());
                                    }
                                } catch (Exception error) {
                                }
                            }
                            newName.put("longitude", longitude);
                            newName.put("latitude", latitude);
                            Log.e("rew",user_data.getString("nickname"));
                            db.insert("USERS", null, newName);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //getAllUsersFromDatabase();



                    //getMore100FromUsers(db);

                    getAllUsersFromDatabase();
                    getFirstIdFromDatabase(db);




                }
            }


        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("rew", "post fail " + new String(error.networkResponse.data));
                //Log.i("Response","nickname"+ ANickname+"pass"+APassword+"Country"+ACountryName+"State"+AState+"city"+ACity+"year"+AYear);

            }
        };

        JsonArrayRequest getRequest = new JsonArrayRequest( url, success, failure);
        VolleyQueue.instance(context).add(getRequest);
    }


   /* public void setListView(final Context context){
        CustomAdapter adapter = new CustomAdapter(userDao, context);
        listView.setAdapter(adapter);

    }*/

    public void setListView(ListView view){
        listView = view;
    }


    public ArrayList<User> getAllUsersFromDatabase(){

        // userDao.clear();
        if(userDao!=null){
            userDao.clear();
            Log.e("clear","User Cleared!");}

        Cursor mCursor =
                db.query(true, "USERS", new String[] {
                                "nick_name",
                                "longitude",
                                "longitude",
                                "timestamp",
                                "city",
                                "state",
                                "country",
                                "year",
                                "id",},null,
                        null,
                        null, null, "id" , null);
        queryCount++;
        Log.e("rew","-----------------from database----"+queryCount+"---------- +"+mCursor.getCount()+"---------");


        if (mCursor.moveToFirst()) {
            do {
                Log.e("rew","-----------------from database-------------- +-------"+mCursor.getCount()+"---------------"+mCursor.getString(mCursor.getColumnIndexOrThrow("id")));
                User user = new User();
                user.setCity(mCursor.getString(mCursor.getColumnIndexOrThrow("city")));
                user.setState(mCursor.getString(mCursor.getColumnIndexOrThrow("state")));
                user.setNickname(mCursor.getString(mCursor.getColumnIndexOrThrow("nick_name")));
                user.setCountry(mCursor.getString(mCursor.getColumnIndexOrThrow("country")));
                user.setYear(Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow("year"))));
                //   user.id = Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow("id")));
                // user.setId(Integer.parseInt(mCursor.getString(mCursor.getColumnIndexOrThrow("id"))));
                userDao.add(user);




                // Collections.sort(userDao, );


                //  userDao.add(user);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        Log.e("rew","userDaoSize"+userDao.size());
        int i=0;
        reverse.clear();
        for(i = userDao.size()-1;i>=0;i--){
            reverse.add(userDao.get(i));
        }

        Log.e("rew","userDaoSizeReverse"+reverse.size());
       /* for(i = 0;i<reverse.size();i++){
            userDao.add(reverse.get(i));
        }*/
        userDao.clear();
        userDao = reverse;
        ListViewAdapter adapter = new ListViewAdapter(userDao, context);
        Log.e("scroll","Scroll position-----------------------"+scrollPosition);
        listView.setSelection(scrollPosition);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisibleItem1;
            int visibleItemCount1;
            int totalItemCount1;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(firstVisibleItem1+visibleItemCount1 == totalItemCount1 && totalItemCount1!=0 && scrollState==SCROLL_STATE_IDLE)
                {
                    Log.e("rew","-load now-"+firstVisibleItem1+"--"+visibleItemCount1+"--total"+totalItemCount1);
                    /*if(!countryString.equals("Select Country") && !yearString.equals("")){*/
                    if(countryString.equals("Select Country") && yearString.equals("") ) {
                        Log.e("rew", countryString + " year -- "+ yearString);
                        scrollPosition=totalItemCount1;
                        getMore100FromUsers(db);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstVisibleItem1 = firstVisibleItem;
                visibleItemCount1 = visibleItemCount;
                totalItemCount1 = totalItemCount;

            }
        });


        return userDao;
    }

    public void setContextListViewDatabase(SQLiteDatabase db1, Context contextq, ListView listView1) {

        db = db1;
        context = contextq;
        listView = listView1;


    }


}