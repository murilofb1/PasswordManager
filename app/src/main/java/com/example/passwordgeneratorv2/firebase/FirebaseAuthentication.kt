package com.example.passwordgeneratorv2.firebase

import android.content.Context
import com.example.passwordgeneratorv2.constants.KEI
import com.example.passwordgeneratorv2.helpers.SharedPreferencesUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthentication {

    private val instance = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null
    fun getCurrentUserUid(): String {
        isSomeoneLogged()
        return currentUser!!.uid
    }

    fun isSomeoneLogged(): Boolean {
        if (currentUser == null) {
            currentUser = instance.currentUser
        }
        return currentUser != null
    }

    fun deleteUser(completeListener: OnCompleteListener<Void>) {
        instance.currentUser!!.delete().addOnCompleteListener(completeListener)
    }


    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        context: Context
    ): Task<AuthResult> {
        return instance.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            val prefUtil = SharedPreferencesUtil(context, KEI.AUTH_PREFERENCE, Context.MODE_PRIVATE)
            prefUtil.addBoolean(KEI.AUTH_STATUS_KEY, true)
        }
    }

    fun registerWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return instance.createUserWithEmailAndPassword(email, password)
    }

    fun signOutUser(context: Context) {
        instance.signOut()
        val prefUtil = SharedPreferencesUtil(context, KEI.AUTH_PREFERENCE, Context.MODE_PRIVATE)
        prefUtil.removeItem(KEI.AUTH_STATUS_KEY)
    }
}