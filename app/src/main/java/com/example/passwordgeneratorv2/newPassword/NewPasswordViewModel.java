package com.example.passwordgeneratorv2.newPassword;

import android.media.MediaPlayer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.passwordgeneratorv2.firebase.PasswordsDB;
import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.helpers.PasswordGenerator;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Observable;


public class NewPasswordViewModel extends ViewModel {
    private ArrayList<Password> spinnerPasswordList = new ArrayList<>();
    private ArrayList<WebsiteModel> modelsList = new ArrayList<>();
    private ValueEventListener spinnerEventListener;
    private static DatabaseReference spinnerItensReference;
    private static Query spinnerItensQuery;
    public static final String SPINNER_LIST_ARG = "slArg";

    private MutableLiveData<Boolean> registrationCompleted = new MutableLiveData<>(false);
    LiveData<Boolean> isRegistrationCompleted() { return registrationCompleted; }

    public PasswordGenerator currentPassword = new PasswordGenerator();
    PasswordsDB passwordsDB = new PasswordsDB();

    public void registerPassword(Password password) {
        passwordsDB.registerPassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                registrationCompleted.setValue(true);
            }
        });
    }

}
/*
    public ArrayList<WebsiteModel> getSpinnerPasswordList() {
        if (spinnerEventListener == null) {
            spinnerItensReference = FirebaseHelper.getUserIconsReference();
            spinnerItensQuery = FirebaseHelper.getUserIconsReference().orderByChild("beingUsed").equalTo(false);

            spinnerItensReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        FirebaseHelper.loadDefaultIcons();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            spinnerEventListener = spinnerItensQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    modelsList.clear();
                    for (DataSnapshot item : snapshot.getChildren()) {
                        modelsList.add(item.getValue(WebsiteModel.class));
                    }
                    modelsList.add(new WebsiteModel("New item", "", ""));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return modelsList;
    }

 */


