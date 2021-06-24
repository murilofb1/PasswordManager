package com.example.passwordgeneratorv2.helpers

import android.content.Context
import android.widget.Toast


//AppCompatActivity can be replaced by Activity
class ToastH(var context: Context) {
    private var toast: Toast

    init {
        toast = Toast(context)
    }

    fun showToast(message: String) {
        if (message.isNotEmpty()) {
            toast.cancel()
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

}