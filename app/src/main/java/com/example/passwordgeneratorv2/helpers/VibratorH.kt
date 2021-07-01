package com.example.passwordgeneratorv2.helpers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibratorH(context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun shortVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    }

}