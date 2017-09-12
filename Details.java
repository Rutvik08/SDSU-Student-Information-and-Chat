package com.example.rutvik.chat;

/**
 * Created by Rutvik on 4/11/2017.
 */

public class Details {
    public String nickname;
    public String email;

    public Details() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Details(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

