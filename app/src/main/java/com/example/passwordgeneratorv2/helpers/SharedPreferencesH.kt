package com.example.passwordgeneratorv2.helpers

import android.content.Context

class SharedPreferencesUtil(context: Context, name: String, mode: Int) {

    private val sharedPreference = context.getSharedPreferences(name, mode)
    private val editor = sharedPreference.edit()

    fun getBoolean(key: String): Boolean = sharedPreference.getBoolean(key, false)

    fun addBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
    }

    fun removeItem(key: String) {
        editor.remove(key)
    }
}