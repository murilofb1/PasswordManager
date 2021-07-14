package com.example.passwordgeneratorv2.authentication.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.firebase.FirebaseAuthentication

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val toastMessage = MutableLiveData<String>()
    fun getToastMessage(): LiveData<String> = toastMessage

    private val registrationSuccess = MutableLiveData<Boolean>()
    fun wasRegistrationSuccessful(): LiveData<Boolean> = registrationSuccess

    fun registerWithEmailAndPassword(email: String, password: String, password2: String) {
        when{
            password != password2 -> toastMessage.value = getApplication<Application>().getString(R.string.passwords_dont_match)
            email.isEmpty() && password.isEmpty() && password2.isEmpty() -> getApplication<Application>().getString(R.string.fill_all_the_fields_first)
            email.isEmpty() -> getApplication<Application>().getString(R.string.fill_your_email_first)
            password.isEmpty()  -> getApplication<Application>().getString(R.string.fill_your_password_first)
            password2.isEmpty()  -> getApplication<Application>().getString(R.string.fill_your_password_confirmation_first)
            else ->{
                FirebaseAuthentication().registerWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) registrationSuccess.value = true
                }
            }
        }
    }

}