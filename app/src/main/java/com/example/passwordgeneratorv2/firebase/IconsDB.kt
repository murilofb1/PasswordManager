package com.example.passwordgeneratorv2.firebase

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
}