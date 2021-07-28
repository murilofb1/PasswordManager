package com.example.passwordgeneratorv2.dialogs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.to_delete.NewCustomDialogs
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.constants.IntentTags
import com.example.passwordgeneratorv2.databinding.DialogPasswordInfoNewBinding
import com.example.passwordgeneratorv2.edit_password.EditPasswordActivity
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.ClipboardH
import com.example.passwordgeneratorv2.helpers.ToastH
import com.example.passwordgeneratorv2.models.Password
import com.google.android.material.snackbar.Snackbar

class EditPasswordDialog(private val context: Context) : CustomDialogsNew(context) {

    override fun setPassword(password: Password) {
        this.password = password
        buildSnack()
        buildDialog()
    }

    private fun buildDialog() {
        val decodedPassword = Base64H.decode(password!!.password)
        val infoDialogBinding = DialogPasswordInfoNewBinding.inflate(
            LayoutInflater.from(context)
        )


        with(infoDialogBinding) {
            txtInfoSiteName.text = password!!.siteName
            txtInfoSitePassword.text = decodedPassword
            Glide.with(context)
                .load(password!!.iconLinkToUri())
                .placeholder(R.drawable.default_image)
                .into(imgInfoSiteIcon)

            txtInfoSiteLink.setOnClickListener {
                if (password?.siteLink == "") toastH?.showToast(context.getString(R.string.no_link_assigned))
                else {
                    try {
                        val openSite = Intent(Intent.ACTION_VIEW);
                        openSite.data = Uri.parse(password!!.siteLink);
                        context.startActivity(openSite);
                    } catch (e: Exception) {
                        toastH?.showToast(context.getString(R.string.unable_to_open_site))
                    }
                }
            }

            btnInfoCopy.setOnClickListener {
                ClipboardH.copyString(decodedPassword, context)
                toastH?.showToast(
                    context.getString(R.string.toast_copy_password, password!!.siteName)
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
                dialog.dismiss()
                callback1?.onPasswordRemoved(password!!)
                snack.show()

            }
            btnInfoDelete.setOnLongClickListener {
                toastH?.showToast(context.getString(R.string.delete_password))
                true
            }
        }
        dialog.setView(infoDialogBinding.root);
    }

    private var undoClicked = false
    private lateinit var snack: Snackbar
    private fun buildSnack() {
        val activity = context as Activity
        snack = Snackbar.make(
            activity.findViewById(android.R.id.content),
            context.getString(R.string.password_removed),
            Snackbar.LENGTH_SHORT
        )
        snack.setAction("Undo") { undoClicked = true }

    }


    var callback1: PasswordStatusCallback? = null
    fun setPasswordStatusCallback(callback: PasswordStatusCallback) {
        callback1 = callback
        addSnackCallback()
    }

    private fun addSnackCallback() {
        snack.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (undoClicked) {
                    true -> if (password != null) callback1?.onPasswordRestored(password!!)
                    false -> PasswordsDB().movePasswordToDeleted(password!!)
                }
                super.onDismissed(transientBottomBar, event)
            }
        })
    }

}


