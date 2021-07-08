package com.example.passwordgeneratorv2.helpers

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.passwordgeneratorv2.R


class BiometricH {
    companion object {
        fun showUnlockBiometric(activity: FragmentActivity) {
            val executor = ContextCompat.getMainExecutor(activity)

            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Security.turnLock(true)
                    }
                }
            )
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.biometric_unlock_title))
                .setDescription(activity.getString(R.string.biometric_unlock_description))
                .setDeviceCredentialAllowed(true)
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }
}