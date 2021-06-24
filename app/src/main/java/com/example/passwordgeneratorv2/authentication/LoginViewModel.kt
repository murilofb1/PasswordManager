package com.example.passwordgeneratorv2.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.passwordgeneratorv2.R
import androidx.lifecycle.MutableLiveData
import com.example.passwordgeneratorv2.firebase.FirebaseAuthentication

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val toastMessage = MutableLiveData<String?>()
    fun getToastMessage(): MutableLiveData<String?> = toastMessage
    private val loginSuccess = MutableLiveData<Boolean>()
    fun wasLoginSuccessful(): LiveData<Boolean> = loginSuccess
    private val auth = FirebaseAuthentication()

    fun login(email: String, password: String) {
        toastMessage.value = when {
            email.isEmpty() && password.isEmpty() -> getApplication<Application>().getString(R.string.fill_all_the_fields_first)
            email.isEmpty() && password.isNotEmpty() -> getApplication<Application>().getString(R.string.fill_your_email_first)
            email.isNotEmpty() && password.isEmpty() -> getApplication<Application>().getString(R.string.fill_your_password_first)
            else -> null
        }
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.loginWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) loginSuccess.value = true
                else toastMessage.value = it.exception?.message
            }
        }
    }

}
