package com.example.passwordgeneratorv2.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthentication {

    private val instance = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null

    fun isSomeoneLogged(): Boolean {
        if (currentUser == null) {
            currentUser = instance.currentUser
        }
        return currentUser != null
    }

    fun loginWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return instance.signInWithEmailAndPassword(email, password)
    }

    fun registerWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return instance.createUserWithEmailAndPassword(email, password)
    }
}