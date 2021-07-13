package com.example.passwordgeneratorv2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.constants.IntentTags
import com.example.passwordgeneratorv2.databinding.DialogPasswordInfoNewBinding
import com.example.passwordgeneratorv2.editPassword.EditPasswordActivity
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.ClipboardH
import com.example.passwordgeneratorv2.helpers.ToastH
import com.example.passwordgeneratorv2.models.Password
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class CustomDialogs(val context: Context) {
    private val dialog = MaterialAlertDialogBuilder(context, R.style.RoundedDialogStyle).create()
    private var password: Password? = null
    private var dialogConfigured = false
    private var toastH: ToastH? = null

    companion object {
        const val EDIT_PASSWORD = "edit_psswd"
        const val SHOW_INFO = "show_info"
    }

    fun setPassword(psswd: Password): CustomDialogs {
        this.password = psswd
        return this;
    }

    fun activateToastMessages(): CustomDialogs {
        toastH = ToastH(context)
        return this;
    }

    fun setDialogType(dialogType: String): CustomDialogs {
        when (dialogType) {
            EDIT_PASSWORD -> configureEditPasswordDialog()
            else -> configurePasswordInfoDialog()
        }
        return this
    }

    fun showDialog() {
        if (!dialogConfigured) throw IllegalStateException("The type of the CustomDialog is not configured")
        dialog.show()
    }

    private fun configureEditPasswordDialog() {
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
            }
        }
        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            context.getString(R.string.negative_button)
        ) { _, _ -> dialog.dismiss() }
        dialogConfigured = true
    }

    val passwordRemoved = MutableLiveData<Password>()
    private fun configurePasswordInfoDialog() {
        val decodedPassword = Base64H.decode(password?.password)
        val infoDialogBinding = DialogPasswordInfoNewBinding.inflate(
            LayoutInflater.from(context)
        )
        dialog.setView(infoDialogBinding.root);

        with(infoDialogBinding) {
            txtInfoSiteName.text = password?.siteName
            txtInfoSitePassword.text = decodedPassword
            Glide.with(context)
                .load(password?.iconLinkToUri())
                .placeholder(R.drawable.default_image)
                .into(imgInfoSiteIcon)

            btnInfoCopy.setOnClickListener {
                ClipboardH.copyString(decodedPassword, context)
                toastH?.showToast(
                    context.getString(R.string.toast_copy_password, password?.siteName)
                )
            }
            btnInfoCopy.setOnLongClickListener {
                toastH?.showToast(context.getString(R.string.copy_password))
                true
            }

            btnInfoEdit.setOnClickListener {
                val i = Intent(context, EditPasswordActivity::class.java)
                i.putExtra(IntentTags.EXTRA_PASSWORD, password!!)
                context.startActivity(i)
            }
            btnInfoEdit.setOnLongClickListener {
                toastH?.showToast(context.getString(R.string.edit_password))
                true
            }

            btnInfoDelete.setOnClickListener {
                //PasswordsDB().movePasswordToDeleted(password!!)
                removeSnackBar()
                passwordRemoved.value = password
                dialog.dismiss()
            }
            btnInfoDelete.setOnLongClickListener {
                toastH?.showToast(context.getString(R.string.delete_password))
                true
            }
        }

        dialogConfigured = true
    }

    val undoStatus = MutableLiveData(false)
    private fun removeSnackBar() {
        val activity = context as Activity
        val snack = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "Password removed",
            Snackbar.LENGTH_SHORT
        )
        snack.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (undoStatus.value == false) {
                    PasswordsDB().movePasswordToDeleted(password!!)
                }
                undoStatus.value == false
                super.onDismissed(transientBottomBar, event)
            }
        })
        snack.setAction("Undo") {
            undoStatus.value = true
        }
        snack.show()
    }


}