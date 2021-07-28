package com.example.passwordgeneratorv2.dialogs

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.firebase.IconsDB
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.firebase.StorageDB
import com.example.passwordgeneratorv2.models.Password

class ConfirmEditDialog(val context: Context) : CustomDialogsNew(context) {
    override fun setPassword(password: Password, iconUri: Uri?) {
        super.setPassword(password)
        configureEditPasswordDialog(iconUri)
    }

    companion object {
        var callback: OnOptionSelected? = null
        fun setOnButtonPressedListener(listener: OnOptionSelected?) {
            this.callback = listener
        }
    }

    private fun configureEditPasswordDialog(iconUri: Uri?) {
        dialog.setTitle(context.getString(R.string.edt_password_dialog_title))
        dialog.setMessage(context.getString(R.string.edt_password_dialog_message))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getString(R.string.positive_button)
        ) { _, _ ->
            if (password != null) {
                PasswordsDB().updatePassword(password!!).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val activity = context as Activity
                        activity.finish()
                    }
                }
                IconsDB().addCustomPasswordIcon(password!!.id, iconUri)
            }
            callback?.onPositivePressed()
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.negative_button)
        ) { _, _ -> dialog.dismiss() }
    }

    interface OnOptionSelected {
        fun onPositivePressed()
        fun onNegativePressed() {}
    }
}