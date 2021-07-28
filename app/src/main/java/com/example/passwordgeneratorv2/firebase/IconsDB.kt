package com.example.passwordgeneratorv2.firebase

import android.net.Uri
import com.example.passwordgeneratorv2.constants.FirebaseKeys
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class IconsDB : FirebaseDB() {

    private fun getDefaultIconsReference(): DatabaseReference {
        val reference = rootReference.child(FirebaseKeys.DEFAULT_ICONS_KEY);
        return reference;
    }

    fun loadWebsites(listener: ValueEventListener) {
        val reference = getDefaultIconsReference();
        reference.addListenerForSingleValueEvent(listener)
    }

    fun addCustomPasswordIcon(passwordID: String, imageUri: Uri?) {
        if (imageUri != null) {
            StorageDB.uploadEditImage(passwordID, imageUri, object : StorageDB.OnUploadComplete {
                override fun onComplete(iconLink: String, iconPath: String) {
                    getCurrentUserReference()
                        .child(FirebaseKeys.PASSWORDS_KEY)
                        .child(passwordID)
                        .child(FirebaseKeys.ICON_LINK_KEY)
                        .setValue(iconLink)

                    getCurrentUserReference()
                        .child(FirebaseKeys.CUSTOM_ICONS_KEY)
                        .push()
                        .setValue(iconPath);
                }
            },
            )
        }
    }
}