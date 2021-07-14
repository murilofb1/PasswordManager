package com.example.passwordgeneratorv2.to_delete;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.passwordgeneratorv2.firebase.PasswordsDB;
import com.example.passwordgeneratorv2.models.Password;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class DeletedPasswordsViewModel extends ViewModel {

    private MutableLiveData<List<Password>> passwordList = new MutableLiveData<>();
    public LiveData<List<Password>> getPasswordList() { return passwordList; }

    public static int ARG_LIST_UPDATE = 0;

    private PasswordsDB passwordsDB = new PasswordsDB();

    private void loadList() {

        passwordsDB.loadDeletedPasswords(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Password> list = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    list.add(item.getValue(Password.class));
                }
                passwordList.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        listenerDeletedPasswords = deletedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                passwordList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    passwordList.add(item.getValue(Password.class));
                }
                notifyUpdates(ARG_LIST_UPDATE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
         */
    }

    public void detachListeners() {
        passwordsDB.removeAllListeners();
    }
}
