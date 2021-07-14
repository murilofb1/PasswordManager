package com.example.passwordgeneratorv2.to_delete;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.passwordgeneratorv2.authentication.AuthenticationActivity;
import com.example.passwordgeneratorv2.models.Password;
import com.example.passwordgeneratorv2.models.WebsiteModel;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;

public class FirebaseHelper {
    private static FirebaseAuth auth;
    private static StorageReference rootStorageReference;
    private static DatabaseReference rootReference;

/*
    public static void editName(String newName) {
        getUserDataReference().child("name").setValue(newName);
    }

 */
    /*
    public static FirebaseAuth getFirebaseAuth() {
        if (auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }
    public static void editEmail(String newEmail) {
        getUserDataReference().child("email").setValue(newEmail);
        getFirebaseAuth().getCurrentUser().updateEmail(newEmail);
    }

    public static void editPassword(String newPassword) {
        getUserDataReference().child("password").setValue(newPassword);
        getFirebaseAuth().getCurrentUser().updatePassword(newPassword);
    }

     */

    /*
    private static DatabaseReference getRootDatabaseReference() {
        if (rootReference == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            rootReference = FirebaseDatabase.getInstance().getReference();
            rootReference.keepSynced(true);
        }
        return rootReference;
    }

     */

    // Referência ususário no realtime database
    /*
    public static DatabaseReference getUserDatabaseReference() {
        final DatabaseReference userReference = getRootDatabaseReference()
                .child()
                .child(getFirebaseAuth().getUid());
        return userReference;
    }

     */

    //Referência das senhas do usuário
/*
    public static DatabaseReference getDefaultIconsReference() {
        final DatabaseReference iconReference = getRootDatabaseReference().child("default_icons");
        return iconReference;
    }

 */

    /*
    public static void loadDefaultIcons() {
        DatabaseReference reference = FirebaseHelper.getDefaultIconsReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    WebsiteModel model = item.getValue(WebsiteModel.class);
                    getUserIconsReference().child(model.getName()).setValue(model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     */

    /*
    public static void deletePassword(Password password) {
        getUserDatabaseReference().child("deletedPasswords").child(password.getSite()).setValue(password);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long deleteTime = timestamp.getTime() + 2629800000L;
        getUserDatabaseReference().child("deletedPasswords").child(password.getSite()).child("deletedTime").setValue(deleteTime);
    }
    */


    /*
    public static void deleteUser(Activity activity) {
        getUserDatabaseReference().child("customIconsPath").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    StorageReference reference = FirebaseStorage.getInstance().getReference(item.getValue().toString());
                    reference.delete();
                }
                getUserDatabaseReference().removeValue();
                FirebaseHelper.getFirebaseAuth().getCurrentUser().delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity, "Your account is now deleted", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(activity.getApplicationContext(), AuthenticationActivity.class);
                        activity.startActivity(i);
                        activity.finishAffinity();
                        Log.i("deleteStatus", "success");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

     */

    // passar tabmém a activity para poder fechar a mesma quando a senha for cadastrada com sucesso




    // Referência ususário no DatabaseStorage
    /*
    private static StorageReference getUserStorageReference() {
        String userUID = getFirebaseAuth().getUid();
        StorageReference userStorage = getRootStorageReference().child("icons/" + userUID + "/");
        return userStorage;
    }

     */
/*
    // fazer upload da imagem
    public static void uploadSpinnerImage(String siteName, Uri imageUri, Activity activity) {
        StorageReference imageReference = getUserStorageReference().child("spinnerIcons/" + siteName.toLowerCase() + ".png");
        imageReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getUserIconsReference().child(siteName).child("name").setValue(siteName);
                getUserIconsReference().child(siteName).child("beingUsed").setValue(false);
                getUserIconsReference().child(siteName).child("siteLink").setValue("");
                imageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                    getUserIconsReference().child(siteName).child("iconLink").setValue(task1.getResult().toString()).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Toast.makeText(activity, "New icon added", Toast.LENGTH_SHORT).show();
                        }
                    });

                });
                getUserDatabaseReference().child("customIconsPath").push().setValue(imageReference.getPath());
            }
        });
    }

 */

    /*
    public static void uploadEditImage(String siteName, Uri imageUri) {
        StorageReference imageReference = getUserStorageReference().child("edtIcons/" + siteName.toLowerCase() + ".png");
        imageReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageReference.getDownloadUrl().addOnCompleteListener(task1 -> {
                    getUserPasswordsReference()
                            .child(siteName)
                            .child("iconLink")
                            .setValue(task1.getResult().toString());
                    getUserDatabaseReference().child("customIconsPath").push().setValue(imageReference.getPath());
                });
            }
        });


    }

     */

}
