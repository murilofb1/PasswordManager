package com.example.passwordgeneratorv2.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.passwordgeneratorv2.firebase.PasswordsDB;
import com.example.passwordgeneratorv2.models.Password;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Password>> passwordList = new MutableLiveData<>();
    LiveData<List<Password>> getPasswordList() {
        return passwordList;
    }

    private PasswordsDB passwordsDB = new PasswordsDB();

    public HomeViewModel() {
        passwordsDB.clearPasswordsTrash();
        loadPasswords();
    }

    private void loadPasswords() {
        passwordsDB.loadCurrentUserPasswords(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<Password> list = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    list.add(item.getValue(Password.class));
                }
                passwordList.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    void detachListener() {
        passwordsDB.removeAllListeners();
    }

}
