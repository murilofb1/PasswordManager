package com.example.passwordgeneratorv2.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Security {
    companion object {
        private val appUnlocked = MutableLiveData(false)
        fun getAppUnlockStatus(): LiveData<Boolean> = appUnlocked

        fun turnLock(bool: Boolean) {
            appUnlocked.value = bool
        }
    }
}