package com.example.passwordgeneratorv2.firebase

import com.example.passwordgeneratorv2.helpers.FirebaseHelper
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageDB {
    val rootReference = FirebaseStorage.getInstance().reference

    private fun getUserStorageReference(): StorageReference? {
        val userId = FirebaseAuthentication().getCurrentUserUid()
        return rootReference.child("icons/$userId/")
    }
}