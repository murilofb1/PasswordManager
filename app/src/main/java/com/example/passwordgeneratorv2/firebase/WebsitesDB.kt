package com.example.passwordgeneratorv2.firebase

import com.google.firebase.database.ValueEventListener

class WebsitesDB : FirebaseDB() {

    fun loadWebsites(listener: ValueEventListener) {
        val reference = getCurrentUserReference().child("icons")
        reference.addValueEventListener(listener)
        addConsult(reference, listener)
    }

}