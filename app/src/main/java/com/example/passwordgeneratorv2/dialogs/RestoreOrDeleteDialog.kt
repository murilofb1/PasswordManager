package com.example.passwordgeneratorv2.dialogs

import android.app.Dialog
import android.content.Context
import android.widget.ListView
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.adapters.AdapterDialogPasswords
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.models.Password
import com.example.passwordgeneratorv2.to_delete.CustomDialogs

class RestoreOrDeleteDialog(val context: Context) : CustomDialogsNew(context) {
    companion object {
        const val RESTORE_PASSWORD = 0
        const val DELETE_PASSWORD = 1
    }

    lateinit var passwordsList: List<Password>
    var listView = ListView(context)
    fun setPasswordList(passwords: List<Password>) {
        passwordsList = passwords
        listView.adapter = AdapterDialogPasswords(
            context, R.layout.list_dialog_passwords, passwordsList
        )
        listView.setSelector(android.R.color.transparent)
        dialog.setView(listView)
    }

    fun setDialogType(type: Int) {
        when (type) {
            DELETE_PASSWORD -> buildDeleteDialog()
            RESTORE_PASSWORD -> buildRestoreDialog()
        }
    }

    private var callback: OnActionConfirmation? = null
    fun setOnActionConfirmationCallback(callback: OnActionConfirmation) {
        this.callback = callback
    }


    private val passwordsDB = PasswordsDB()
    private fun buildDeleteDialog() {
        dialog.setMessage(context.getString(R.string.passwords_delete_warning))
        dialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.positive_button))
        { _, _ ->
            for (item in passwordsList) {
                passwordsDB.deletePermanently(item)
                callback?.onConfirmation()
            }
        }
        dialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.negative_button))
        { _, _ -> dialog.dismiss() }
    }

    private fun buildRestoreDialog() {
        dialog.setMessage(context.getString(R.string.passwords_restore_warning))
        dialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.positive_button))
        { _, _ ->
            for (item in passwordsList) {
                passwordsDB.restorePassword(item)
                callback?.onConfirmation()
            }
        }
        dialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.negative_button))
        { _, _ -> dialog.dismiss() }
    }
}