package com.example.passwordgeneratorv2.dialogs

import com.example.passwordgeneratorv2.models.Password

interface OnActionConfirmation {
    fun onConfirmation()
}

interface PasswordStatusCallback {
    fun onPasswordRemoved(password: Password)
    fun onPasswordRestored(password: Password)
}