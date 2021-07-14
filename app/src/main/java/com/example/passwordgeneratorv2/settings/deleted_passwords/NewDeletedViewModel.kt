package com.example.passwordgeneratorv2.settings.deleted_passwords

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.models.Password
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class NewDeletedViewModel : ViewModel() {
    private val deletedPasswords = MutableLiveData<MutableList<Password>>()
    fun getDeletedPasswordsList(): LiveData<MutableList<Password>> = deletedPasswords
    private var selectedItems: Array<Boolean> = emptyArray()
    private val selectedItemCount = MutableLiveData(0)
    fun selectedItemCount(): LiveData<Int> = selectedItemCount

    private val passwordsDB = PasswordsDB()

    init {
        loadDeletedPasswords()
    }

    private fun loadDeletedPasswords() {
        passwordsDB.loadDeletedPasswords(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<Password> = ArrayList()
                for (item in snapshot.children) {
                    list.add(item.getValue(Password::class.java)!!)
                }
                selectedItems = Array(list.size) { false }
                deletedPasswords.value = list
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun selectItemAtPosition(position: Int) {
        if (!selectedItems[position]) {
            selectedItems[position] = true
            selectedItemCount.value.let {
                selectedItemCount.value = it!! + 1
            }
        }
        Log.i("DeletedActivityLog", "SelectedCount = ${selectedItemCount.value}")
    }

    fun unselectItemAtPosition(position: Int) {
        if (selectedItems[position]) {
            selectedItems[position] = false

            selectedItemCount.value.let {
                selectedItemCount.value = it!! - 1
            }
        }
        Log.i("DeletedActivityLog", "SelectedCount = ${selectedItemCount.value}")
    }

    fun isItemSelected(itemPosition: Int): Boolean = selectedItems[itemPosition]

    fun hasSomeItemSelected(): Boolean {
        for (item in selectedItems) {
            if (item) return true
        }
        return false
    }


}