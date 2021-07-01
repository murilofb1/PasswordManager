package com.example.passwordgeneratorv2.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

open class FirebaseDB {

    private val referencesList: MutableList<Query> = ArrayList()
    private val listenersList: MutableList<ValueEventListener> = ArrayList()
    protected val rootReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun generatePushKey(): String = rootReference.key!!

    fun getCurrentUserReference(): DatabaseReference {
        val userId = FirebaseAuthentication().getCurrentUserUid()
        return rootReference.child(FirebaseKeys.USERS_KEY).child(userId)
    }

    protected fun addConsult(reference: Query, eventListener: ValueEventListener) {
        referencesList.add(reference)
        listenersList.add(eventListener)
    }

    fun removeAllListeners() {
        var i = 0
        while (i < referencesList.size) {
            referencesList[i].removeEventListener(listenersList[i])
            i++
        }
    }
}