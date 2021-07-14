package com.example.passwordgeneratorv2.settings.account_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passwordgeneratorv2.firebase.UsersDB
import com.example.passwordgeneratorv2.models.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AccountInfoViewModel : ViewModel() {
    private val userData = MutableLiveData<UserModel>()
    fun getUserData(): LiveData<UserModel> = userData

    private val userDB = UsersDB()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        userDB.getCurrentUserData(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userData.value = snapshot.getValue(UserModel::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun detachListeners() {
        userDB.removeAllListeners()
    }
}