package com.example.passwordgeneratorv2.firebase

import android.net.Uri
import com.example.passwordgeneratorv2.models.Password
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageDB {
    companion object {
        private val rootReference = FirebaseStorage.getInstance().reference
        private fun getUserStorageReference(): StorageReference {
            val userId = FirebaseAuthentication().getCurrentUserUid()
            return rootReference.child("icons/$userId/")
        }

        private var passwordUpdateCallback: OnPasswordUpdated? = null
        fun addOnPasswordUpdateListener(callback: OnPasswordUpdated?) {
            passwordUpdateCallback = callback
        }


        fun uploadEditImage(
            passwordID: String,
            imageUri: Uri,
            uploadComplete: OnUploadComplete
        ) {
            val imageReference = getUserStorageReference().child("edtIcons/$passwordID.png");
            imageReference.putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    imageReference.downloadUrl.addOnCompleteListener { downloadUrl ->
                        uploadComplete.onComplete(
                            downloadUrl.result.toString(),
                            imageReference.path
                        )
                        passwordUpdateCallback?.onUpdate()
                    }
                }
            }


        }


    }


    interface OnUploadComplete {
        fun onComplete(iconLink: String, iconPath: String)
    }

    interface OnPasswordUpdated {
        fun onUpdate()
    }
}