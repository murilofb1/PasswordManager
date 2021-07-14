package com.example.passwordgeneratorv2.firebase

import com.example.passwordgeneratorv2.constants.FirebaseKeys
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
    }

    fun updatePassword(password: Password): Task<Void> {
        return getCurrentUserReference().child(FirebaseKeys.PASSWORDS_KEY).child(password.id)
            .setValue(password.toMap())
    }

    fun loadCurrentUserPasswords(eventListener: ValueEventListener) {
        val reference = getCurrentUserReference()
            .child(FirebaseKeys.PASSWORDS_KEY)
            .orderByChild(FirebaseKeys.PASSWORD_SITE_NAME_KEY)

        reference.addValueEventListener(eventListener)
        addConsult(reference, eventListener)
    }

    fun movePasswordToDeleted(password: Password) {
        getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY).child(password.id)
            .setValue(password)
        val timestamp = Timestamp(System.currentTimeMillis())
        val deleteTime = timestamp.time + 2629800000L//UM MÊS EM LONG
        //Adicionando o deletedTime a referencia da senha deletada
        getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY).child(password.id)
            .child(FirebaseKeys.DELETED_TIME_KEY).setValue(deleteTime)
        //Removendo a senha das senhas principais
        getCurrentUserReference().child(FirebaseKeys.PASSWORDS_KEY).child(password.id).removeValue()
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
            .removeValue()
        //Putting the password on the main
        getCurrentUserReference()
            .child(FirebaseKeys.PASSWORDS_KEY)
            .child(password.id)
            .setValue(password)
        //Removing the deletedTime
        getCurrentUserReference()
            .child(FirebaseKeys.PASSWORDS_KEY)
            .child(password.id)
            .child(FirebaseKeys.DELETED_TIME_KEY)
            .removeValue()
    }

    fun loadDeletedPasswords(eventListener: ValueEventListener) {
        val reference = getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY)
        reference.addValueEventListener(eventListener)
        addConsult(reference, eventListener)
    }

    fun clearPasswordsTrash() {
        val passwordsReference = getCurrentUserReference().child(FirebaseKeys.DELETED_PASSWORDS_KEY)
        passwordsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val password = item.getValue(Password::class.java)
                    val timeNow = Timestamp(System.currentTimeMillis())
                    if (timeNow.time >= password!!.deletedTime) {
                        passwordsReference.child(item.key!!).removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}