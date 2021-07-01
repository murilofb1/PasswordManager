package com.example.passwordgeneratorv2

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.models.Password

class CustomDialogs {
    class ConfirmPasswordEditionDialog(context: Context) {
        private val builder = AlertDialog.Builder(context)
        private var password: Password? = null
        fun setPassword(psswd: Password) {
            this.password = psswd
        }

        init {
            builder.setTitle(context.getString(R.string.edt_password_dialog_title))
            builder.setMessage(context.getString(R.string.edt_password_dialog_message))
            builder.setPositiveButton(context.getString(R.string.positive_button)) { dialog, which ->
                if (password!= null) PasswordsDB().updatePassword(password!!)
            }
            builder.setNegativeButton(context.getString(R.string.negative_button), null)
        }

        fun showDialog() {
            builder.show()
        }
    }
}