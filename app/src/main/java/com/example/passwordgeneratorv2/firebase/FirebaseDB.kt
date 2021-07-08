package com.example.passwordgeneratorv2.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

open class FirebaseDB {

    private var referencesList: MutableList<Query>? = null
    private var listenersList: MutableList<ValueEventListener>? = null
    protected val rootReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    companion object {
        private val staticRoot: DatabaseReference = FirebaseDatabase.getInstance().reference
        fun generatePushKey(): String = staticRoot.push().key!!
    }

    fun getCurrentUserReference(): DatabaseReference {
        val userId = FirebaseAuthentication().getCurrentUserUid()
        return rootReference.child(FirebaseKeys.USERS_KEY).child(userId)
    }

    protected fun addConsult(reference: Query, eventListener: ValueEventListener) {
        if (referencesList == null && listenersList == null) {
            referencesList = ArrayList()
            listenersList = ArrayList()
        }
        referencesList?.add(reference)
        listenersList?.add(eventListener)
    }

    fun removeAllListeners() {
        if (referencesList != null && listenersList != null) {
            var i = 0
            while (i < referencesList!!.size) {
                referencesList!![i].removeEventListener(listenersList!![i])
                i++
            }
        }
    }
}