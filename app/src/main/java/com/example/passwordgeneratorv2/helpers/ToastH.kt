package com.example.passwordgeneratorv2.helpers

import android.content.Context
import android.widget.TextView
import android.widget.Toast


class ToastH(private val context: Context) {
    private var toast = Toast(context)

    fun showToast(message: String) {
        if (message.isNotEmpty()) {
            toast.cancel()
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}