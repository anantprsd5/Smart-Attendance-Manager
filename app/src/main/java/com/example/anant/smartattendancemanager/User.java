package com.example.anant.smartattendancemanager;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by anant on 2/8/18.
 */

@IgnoreExtraProperties
public class User {

    public String email;
    public String name;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

}
