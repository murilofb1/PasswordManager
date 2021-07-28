package com.example.passwordgeneratorv2.to_delete

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.constants.IntentTags
import com.example.passwordgeneratorv2.databinding.DialogPasswordInfoNewBinding
import com.example.passwordgeneratorv2.dialogs.PasswordStatusCallback
import com.example.passwordgeneratorv2.edit_password.EditPasswordActivity
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.ClipboardH
import com.example.passwordgeneratorv2.helpers.ToastH
import com.example.passwordgeneratorv2.models.Password
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class NewCustomDialogs {
    companion object {
        private var dialog: AlertDialog? = null
        internal var password: Password? = null
        private var toastH: ToastH? = null
        var dialogBuilt = false

        class PasswordInfoDialog(val context: Context) {
            fun disableToastMessages() {
                toastH = null
                if (dialogBuilt) buildDialog()
            }


            init {
                dialog = MaterialAlertDialogBuilder(context, R.style.RoundedDialogStyle).create()
                toastH = ToastH(context)
                buildSnack()
            }

            fun setPassword(psswd: Password) {
                password = psswd
                //if (dialogBuilt)
                    buildDialog()
            }

            private fun buildDialog() {
                Log.i("HomeActivity","Starting build")
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
                        /*
                        showSnackBar()
                        Log.i("HomeDialog", "Dialog: Showing Snackbar")

                        dialog?.dismiss()
                        Log.i("HomeDialog", "Dialog: Dialog dismissed")
                         */
                        dialog?.dismiss()
                        callback1?.onPasswordRemoved(password!!)
                        snack.show()

                    }
                    btnInfoDelete.setOnLongClickListener {
                        toastH?.showToast(context.getString(R.string.delete_password))
                        true
                    }
                }
                dialog?.setView(infoDialogBinding.root);
                dialogBuilt = true
                Log.i("HomeActivity","Build Ended")
            }

            fun create(): AlertDialog {
                // if (!dialogBuilt)
                    buildDialog()
                dialogBuilt = false
                return dialog!!
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
                Log.i("HomeDialog", "Snack: Snackbar.make")
                addSnackCallback()
                Log.i("HomeDialog", "Snack: addCallback()")
                snack.setAction("Undo") { undoClicked = true }
                Log.i("HomeDialog", "Snack: setAction()")
                Log.i("HomeDialog", "Snack: show()")
            }

            var callback1: PasswordStatusCallback? = null
            fun setPasswordStatusCallback(callback: PasswordStatusCallback) {
                callback1 = callback
            }

            private fun addSnackCallback() {
                snack.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        when (undoClicked) {
                            true -> {
                                undoClicked = false
                                if (password != null) {
                                    callback1?.onPasswordRestored(password!!)
                                }
                            }
                            false -> {
                                /*
                                if (password!= null){
                                    callback1?.onPasswordRemoved(password!!)
                                }

                                PasswordsDB().movePasswordToDeleted(password!!)
            */
                                Log.i("HomeDialog", "PasswordDB: Password moved to deleted")
                            }
                        }
                        Log.i("HomeDialog", "Snack: Dismissed")
                        super.onDismissed(transientBottomBar, event)
                    }
                })
            }

        }


    }


    /*
    Configuring the dialog according to the passed dialogType
    This function need to be the last called before the show toast
     */
    /*
    fun setDialogType(dialogType: Int): NewCustomDialogs {
        when (dialogType) {
            EDIT_PASSWORD_DIALOG -> configureEditPasswordDialog()
            SHOW_INFO_DIALOG -> configurePasswordInfoDialog()
            else -> configureDeletePasswordsDialog()
        }
        return this
    }

     */

    //Show the dialog after configured
/*
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



    The password info dialog have the option to delete the password (move to the deleted database)
    in this case you have to observe the undoStatus to see if the user changed it's mind


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
                Log.i("HomeDialog", "Dialog: Showing Snackbar")
                passwordRemoved.value = password
                Log.i("HomeDialog", "Dialog: Password removed from adapter")
                dialog.dismiss()
                Log.i("HomeDialog", "Dialog: Dialog dismissed")
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
        Log.i("HomeDialog", "Snack: Snackbar.make")
        snack.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (undoStatus.value) {
                    true -> {
                        undoStatus.value = false
                        Log.i("HomeDialog", "Dialog: undoStatus.value = false")
                    }
                    false -> {

                        PasswordsDB().movePasswordToDeleted(password!!)
                        isRemoving = false
                        Log.i("HomeDialog", "PasswordDB: Password moved to deleted")
                    }
                }
                Log.i("HomeDialog", "Snack: Dismissed")
                super.onDismissed(transientBottomBar, event)
            }
        })
        Log.i("HomeDialog", "Snack: addCallback()")
        snack.setAction("Undo") { undoStatus.value = true }
        Log.i("HomeDialog", "Snack: setAction()")
        snack.show()
        Log.i("HomeDialog", "Snack: show()")
    }


    private var passwordsList: List<Password>? = null
    fun setPasswordList(list: List<Password>): NewCustomDialogs {
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

    fun setDeleteDialogFunction(dialogFunction: Int): NewCustomDialogs {
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
*/

}
