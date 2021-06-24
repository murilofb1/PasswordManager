package com.example.passwordgeneratorv2.home;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.models.Password;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public class HomeViewModel extends Observable {
    private List<Password> passwordList = new ArrayList<>();
    private boolean changed = false;
    public static String ARG_PASSWORD_LIST = "pList";
    private ValueEventListener userPasswordsEventListener;
    private static DatabaseReference userPasswordsReference;
    private static Query orderByFavorite;

    public List<Password> getPasswordList() {
        if (userPasswordsEventListener == null) {
            //orderByFavorite = FirebaseHelper.getUserPasswordsReference().orderByChild("favorite");
            userPasswordsReference = FirebaseHelper.getUserPasswordsReference();
            userPasswordsEventListener = userPasswordsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    passwordList.clear();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        Password password = item.getValue(Password.class);
                        passwordList.add(password);
                    }
                    notifyChange(ARG_PASSWORD_LIST);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        return passwordList;
    }


    private void notifyChange(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
