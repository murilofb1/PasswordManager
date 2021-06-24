package com.example.passwordgeneratorv2.settings.options;

import androidx.annotation.NonNull;

import com.example.passwordgeneratorv2.helpers.FirebaseHelper;
import com.example.passwordgeneratorv2.models.Password;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

class DeletedPasswordsViewModel extends Observable {
    private List<Password> passwordList = new ArrayList<>();
    private DatabaseReference deletedReference = FirebaseHelper.getUserDatabaseReference().child("deletedPasswords");
    private ValueEventListener listenerDeletedPasswords;
    public static int ARG_LIST_UPDATE = 0;

    public List<Password> getPasswordList() {
        return passwordList;
    }

    public void loadList() {
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
    }

    public void removeListener() {
        deletedReference.removeEventListener(listenerDeletedPasswords);
    }


    private void notifyUpdates(Object arg) {
        setChanged();
        notifyObservers(arg);
    }


}
