package com.example.passwordgeneratorv2.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

public class UserModel {
    private String name;
    private String email;
    public static UserModel currentUser;

    public UserModel() {

    }

    public UserModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    @Exclude
    public static void loadCurretUser() {
        FirebaseHelper.getUserDataReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { currentUser = snapshot.getValue(UserModel.class); }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static UserModel getCurrentUser() { return currentUser; }

    public static void updateUserName(String name) {
        currentUser.name = name;
    }

    public static void updateUserEmail(String email) {
        currentUser.email = email;
    }

}
