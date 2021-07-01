package com.example.passwordgeneratorv2.firebase

import com.example.passwordgeneratorv2.models.Password
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.sql.Timestamp

class PasswordsDB : FirebaseDB() {
    fun registerPassword(password: Password): Task<Void> {

        return getCurrentUserReference()
            .child(FirebaseKeys.PASSWORDS_KEY)
            .child(password.id)
            .setValue(password)

        /*

        //NAO SEI MAIS O QUE ESSA MERDA FAZ

    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            getUserIconsReference().child(password.site).child("beingUsed")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            getUserIconsReference().child(password.site).child("beingUsed")
                                .setValue(true)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

         */
    }

    fun updatePassword(password: Password) {
        getCurrentUserReference().child(FirebaseKeys.PASSWORDS_KEY).child(password.id)
            .setValue(password.toMap())
    }

    fun loadCurrentUserPasswords(eventListener: ValueEventListener) {
        val reference = getCurrentUserReference().child(FirebaseKeys.PASSWORDS_KEY)
        reference.addValueEventListener(eventListener)
        addConsult(reference, eventListener)
    }

    fun deletePassword(password: Password) {
        getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY).child(password.id)
            .setValue(password)
        val timestamp = Timestamp(System.currentTimeMillis())
        val deleteTime = timestamp.time + 2629800000L//UM MÊS EM LONG
        getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY).child(password.id)
            .child(FirebaseKeys.DELETED_TIME_KEY).setValue(deleteTime)
    }

    fun deletePermanently(password: Password) {
        getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY).child(password.id)
            .removeValue()
    }

    fun restorePassword(password: Password) {
        //Deleting the password from the deleted
        getCurrentUserReference()
            .child(FirebaseKeys.DELETED_PASSWORDS_KEY)
            .child(password.id)
            .removeValue();
        //Putting the password on the main
        getCurrentUserReference()
            .child(FirebaseKeys.PASSWORDS_KEY)
            .child(password.id)
            .setValue(password);
        //Removing the deletedTime
        getCurrentUserReference()
            .child(FirebaseKeys.PASSWORDS_KEY)
            .child(password.id)
            .child(FirebaseKeys.DELETED_TIME_KEY)
            .removeValue();
    }

    fun loadDeletedPasswords(eventListener: ValueEventListener) {
        val reference = getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY)
        addConsult(reference, eventListener)
    }

    fun clearPasswordsTrash() {
        val passwordsReference = getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY)
        passwordsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val password = item.getValue(Password::class.java)
                    val timeNow = Timestamp(System.currentTimeMillis())
                    if (timeNow.time >= password!!.deletedTime) passwordsReference.child(password.site)
                        .removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}