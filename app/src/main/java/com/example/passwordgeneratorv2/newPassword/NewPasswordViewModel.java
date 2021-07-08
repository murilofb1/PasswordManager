package com.example.passwordgeneratorv2.newPassword;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.passwordgeneratorv2.R;
import com.example.passwordgeneratorv2.firebase.PasswordsDB;
import com.example.passwordgeneratorv2.firebase.WebsitesDB;
import com.example.passwordgeneratorv2.helpers.PasswordGenerator;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class NewPasswordViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> registrationCompleted = new MutableLiveData<>(false);

    public NewPasswordViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    LiveData<Boolean> isRegistrationCompleted() { return registrationCompleted; }

    private MutableLiveData<List<WebsiteModel>> spinnerList = new MutableLiveData<>(new ArrayList<>());
    LiveData<List<WebsiteModel>> getSpinnerList() { return spinnerList; }

    public PasswordGenerator currentPassword = new PasswordGenerator();
    PasswordsDB passwordsDB = new PasswordsDB();
    WebsitesDB websitesDB = new WebsitesDB();

    public void loadSpinnerList() {
        websitesDB.loadWebsites(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<WebsiteModel> list = new ArrayList();
                for (DataSnapshot item : snapshot.getChildren()) {
                    list.add(item.getValue(WebsiteModel.class));
                }
                list.add(new WebsiteModel(
                        getApplication().getString(R.string.custom_item),
                        "",
                        ""
                ));
                spinnerList.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    public void registerPassword(Password password) {
        passwordsDB.registerPassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                registrationCompleted.setValue(true);
            }
        });
    }

    public void detachListeners() {
        passwordsDB.removeAllListeners();
        websitesDB.removeAllListeners();
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


