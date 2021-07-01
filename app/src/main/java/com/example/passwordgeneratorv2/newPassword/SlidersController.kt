package com.example.passwordgeneratorv2.newPassword

import android.util.Log
import com.google.android.material.slider.Slider

class SlidersController(
    private val passwordSizeSlider: Slider,
    private val uppercaseSlider: Slider,
    private val lowercaseSlider: Slider,
    private val specialCharSlider: Slider
) {

    init {
        updateMaxValue()
    }
    //PasswordValidation
    fun validPassword(): Boolean {
        val passwordSize = passwordSizeSlider.value.toInt()
        val charSum = uppercaseSlider.value.toInt() +
                lowercaseSlider.value.toInt() +
                specialCharSlider.value.toInt()
        Log.i("NewPasswordActivity", "PasswordSize = $passwordSize\n charSUm = $charSum")
        return charSum <= passwordSize
    }

    fun charactersEqualsSize(): Boolean {
        val passwordSize = passwordSizeSlider.value.toInt()
        val charSum = uppercaseSlider.value.toInt() +
                lowercaseSlider.value.toInt() +
                specialCharSlider.value.toInt()

        return charSum == passwordSize
    }

    /*
    fun bindTextView(){
    A CADA MUDANÇA NO SLIDER MUDAR TAMBEM NO TEXTVIEW
    }

     */
    var changeListener: Slider.OnChangeListener? = null
    fun setOnChangeListener(listener: Slider.OnChangeListener) {
        passwordSizeSlider.addOnChangeListener(listener)
        uppercaseSlider.addOnChangeListener(listener)
        lowercaseSlider.addOnChangeListener(listener)
        specialCharSlider.addOnChangeListener(listener)
    }

    fun detachListeners() {
        if (changeListener != null) {
            passwordSizeSlider.removeOnChangeListener(changeListener!!)
            uppercaseSlider.removeOnChangeListener(changeListener!!)
            lowercaseSlider.removeOnChangeListener(changeListener!!)
            specialCharSlider.removeOnChangeListener(changeListener!!)
        }
    }

    //Modifying the Sliders
    fun updateMaxValue() {
        uppercaseSlider.valueTo = passwordSizeSlider.value
        lowercaseSlider.valueTo = passwordSizeSlider.value
        specialCharSlider.valueTo = passwordSizeSlider.value
    }

    fun backTheLongestSlider() {
        val v1 = lowercaseSlider.value
        val v2 = uppercaseSlider.value
        val v3 = specialCharSlider.value

        if (v1 >= v2 && v1 >= v3) lowercaseSlider.value = lowercaseSlider.value--
        else if (v2 >= v1 && v2 >= v3) uppercaseSlider.value = uppercaseSlider.value--
        else specialCharSlider.value = specialCharSlider.value--
    }


}