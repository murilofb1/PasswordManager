package com.example.passwordgeneratorv2.helpers

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Security {
    companion object {
        private val appUnlocked = MutableLiveData(false)
        fun getAppUnlockStatus(): LiveData<Boolean> = appUnlocked

        fun turnLock(bool: Boolean) {
            appUnlocked.value = bool
        }

        fun unlockApp(activity: FragmentActivity){
            BiometricH.showUnlockBiometric(activity)
        }

    }
}