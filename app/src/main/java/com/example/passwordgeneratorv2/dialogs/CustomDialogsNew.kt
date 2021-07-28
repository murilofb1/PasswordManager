package com.example.passwordgeneratorv2.dialogs

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.example.passwordgeneratorv2.helpers.ToastH
import com.example.passwordgeneratorv2.models.Password
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class CustomDialogsNew(context: Context) {
    internal var dialog: AlertDialog = MaterialAlertDialogBuilder(context).create()
    internal var password: Password? = null
    internal var toastH: ToastH? = ToastH(context)

    open fun setPassword(password: Password){ this.password = password }

    fun disableToastMessages() { if (toastH!= null) toastH = null }

    fun create() = dialog

    open fun setPassword(password: Password, iconUri: Uri?) {}
}
