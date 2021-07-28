package com.example.passwordgeneratorv2.to_delete

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.adapters.AdapterDialogPasswords
import com.example.passwordgeneratorv2.constants.IntentTags
import com.example.passwordgeneratorv2.databinding.DialogPasswordInfoNewBinding
import com.example.passwordgeneratorv2.edit_password.EditPasswordActivity
import com.example.passwordgeneratorv2.firebase.PasswordsDB
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.ClipboardH
import com.example.passwordgeneratorv2.helpers.ToastH
import com.example.passwordgeneratorv2.models.Password
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class CustomDialogs(val context: Context) {

    private val dialog = MaterialAlertDialogBuilder(context, R.style.RoundedDialogStyle).create()
    private var password: Password? = null
    private var dialogConfigured = false
    private var toastH: ToastH? = null

    companion object {
        const val EDIT_PASSWORD_DIALOG = 0
        const val SHOW_INFO_DIALOG = 1
        const val DELETE_ACTIVITY_DIALOG = 2
        const val RESTORE_PASSWORD_FUNCTION = 3
        const val DELETE_PASSWORD_FUNCTION = 4
    }

    fun setPassword(psswd: Password): CustomDialogs {
        this.password = psswd
        return this;
    }

    fun activateToastMessages(): CustomDialogs {
        toastH = ToastH(context)
        return this;
    }

    /*
    Configuring the dialog according to the passed dialogType
    This function need to be the last called before the show toast
     */
    fun setDialogType(dialogType: Int): CustomDialogs {
        when (dialogType) {
            EDIT_PASSWORD_DIALOG -> configureEditPasswordDialog()
            SHOW_INFO_DIALOG -> configurePasswordInfoDialog()
            else -> configureDeletePasswordsDialog()
        }
        return this
    }

    //Show the dialog after configured
    fun showDialog() {
        if (!dialogConfigured) {
            throw IllegalStateException("The type of the CustomDialog is not configured")
        }
        dialog.show()
    }

    //Configuring the dialog to confirm editions on the password
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

    /*
    The password info dialog have the option to delete the password (move to the deleted database)
    in this case you have to observe the undoStatus to see if the user changed it's mind
     */

    val passwordRemoved = MutableLiveData<Password>()
    val undoStatus = MutableLiveData(false)
    var isRemoving = false

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

                removeSnackBar()
                Log.i("HomeDialog","Dialog: Showing Snackbar")
                passwordRemoved.value = password
                Log.i("HomeDialog","Dialog: Password removed from adapter")
                dialog.dismiss()
                Log.i("HomeDialog","Dialog: Dialog dismissed")
            }
            btnInfoDelete.setOnLongClickListener {
                toastH?.showToast(context.getString(R.string.delete_password))
                true
            }
        }

        dialogConfigured = true
    }

    private fun removeSnackBar() {
        val activity = context as Activity

        val snack = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "Password removed",
            Snackbar.LENGTH_SHORT
        )
        Log.i("HomeDialog","Snack: Snackbar.make")
        snack.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (undoStatus.value) {
                    true -> {
                        undoStatus.value = false
                        Log.i("HomeDialog","Dialog: undoStatus.value = false")
                    }
                    false -> {

                        PasswordsDB().movePasswordToDeleted(password!!)
                        isRemoving = false
                        Log.i("HomeDialog","PasswordDB: Password moved to deleted")
                    }
                }
                Log.i("HomeDialog","Snack: Dismissed")
                super.onDismissed(transientBottomBar, event)
            }
        })
        Log.i("HomeDialog","Snack: addCallback()")
        snack.setAction("Undo") { undoStatus.value = true }
        Log.i("HomeDialog","Snack: setAction()")
        snack.show()
        Log.i("HomeDialog","Snack: show()")
    }


    private var passwordsList: List<Password>? = null
    fun setPasswordList(list: List<Password>): CustomDialogs {
        passwordsList = list
        return this
    }

    //Delete passwords Dialog
    private fun configureDeletePasswordsDialog() {
        dialog.setTitle(context.getString(R.string.are_you_sure))

        //Configuring Dialog List View
        val listView = ListView(context)
        listView.adapter = AdapterDialogPasswords(
            context, R.layout.list_dialog_passwords, passwordsList!!
        )
        listView.setSelector(android.R.color.transparent)

        dialog.setView(listView)
        dialog.setButton(
            Dialog.BUTTON_NEGATIVE,
            context.getString(R.string.negative_button)
        ) { _, _ -> }
        dialogConfigured = true
    }

    fun setDeleteDialogFunction(dialogFunction: Int): CustomDialogs {
        val passwordsDB = PasswordsDB()
        when (dialogFunction) {
            DELETE_PASSWORD_FUNCTION -> {
                dialog.setMessage(context.getString(R.string.passwords_delete_warning))

                dialog.setButton(
                    Dialog.BUTTON_POSITIVE,
                    context.getString(R.string.positive_button)
                ) { _, _ ->
                    for (item in passwordsList!!) passwordsDB.deletePermanently(item)
                }
            }
            RESTORE_PASSWORD_FUNCTION -> {
                dialog.setMessage(context.getString(R.string.passwords_restore_warning))
                dialog.setButton(
                    Dialog.BUTTON_POSITIVE,
                    context.getString(R.string.positive_button)
                ) { _, _ ->
                    for (item in passwordsList!!) passwordsDB.restorePassword(item)
                }
            }
        }
        return this
    }
}
