package com.example.rutvik.chat;

/**
 * Created by Rutvik on 4/9/2017.
 */

public class User {
    public String nickname, country, state, city, email, password;
    int year;
    double latitude, longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public User(String nickname, String country, String state, String city, int year, double latitude, double longitude) {
        this.nickname = nickname;
        this.country = country;
        this.state = state;

        this.city = city;
        this.year = year;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User(String nickname, String country, String state, String city, int year) {

        this.nickname = nickname;
        this.country = country;
        this.state = state;
        this.city = city;
        this.year = year;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public User(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public User() {
    }

    public User(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}
