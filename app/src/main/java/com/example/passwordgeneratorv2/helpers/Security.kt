package com.example.passwordgeneratorv2.helpers

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Security {
    companion object {
        private val appUnlocked = MutableLiveData(true)
        fun getUnlockStatus(): LiveData<Boolean> = appUnlocked
        fun isAppUnlocked(): Boolean = appUnlocked.value!!

        fun lockApp() {
            appUnlocked.value = false
        }

       internal fun setUnlockStatus(value: Boolean) {
            appUnlocked.value = value
        }

        fun unlockApp(activity: FragmentActivity, void: (() -> Unit)? = null) {
            BiometricH.showUnlockBiometric(activity, void)
        }



    }
}