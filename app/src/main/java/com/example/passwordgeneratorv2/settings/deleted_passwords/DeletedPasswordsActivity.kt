package com.example.passwordgeneratorv2.settings.deleted_passwords

import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordgeneratorv2.to_delete.CustomDialogs
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.adapters.AdapterDialogPasswords
import com.example.passwordgeneratorv2.adapters.NewDeletedPasswordsAdapter
import com.example.passwordgeneratorv2.databinding.ActivityDeletedPasswordsBinding
import com.example.passwordgeneratorv2.dialogs.OnActionConfirmation
import com.example.passwordgeneratorv2.dialogs.RestoreOrDeleteDialog
import com.example.passwordgeneratorv2.helpers.OnRecyclerItemClick
import com.example.passwordgeneratorv2.helpers.Security
import com.example.passwordgeneratorv2.helpers.ToastH

class DeletedPasswordsActivity : AppCompatActivity() {


    lateinit var binding: ActivityDeletedPasswordsBinding
    private val model: NewDeletedViewModel by viewModels()
    private val deletedPasswordsAdapter = NewDeletedPasswordsAdapter()

    private var menuRestorePasswords: MenuItem? = null
    private var menuDeletePasswords: MenuItem? = null
    private var menuUnlockApp: MenuItem? = null
    private var menuTogglePasswordsVisibility: MenuItem? = null

    private lateinit var toastH: ToastH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeletedPasswordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toastH = ToastH(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addObservers()
        initRecycler()
    }

    var isSelectingItems = false
    var isMaxCountSelected = false

    private fun addObservers() {
        model.getDeletedPasswordsList().observe(this) {
            deletedPasswordsAdapter.updateList(it)
            menuTogglePasswordsVisibility?.isVisible = it.isNotEmpty()
        }

        deletedPasswordsAdapter.getSelectedItemCount().observe(this) {
            menuRestorePasswords?.isVisible = it > 0
            menuDeletePasswords?.isVisible = it > 0
            isMaxCountSelected = it >= AdapterDialogPasswords.MAX_ITEM_COUNT
            isSelectingItems = it > 0
        }
        deletedPasswordsAdapter.getPasswordVisibilityStatus().observe(this) {
            when (it) {
                true -> {
                    menuTogglePasswordsVisibility?.title = getString(R.string.hide_passwords)
                }
                else -> {
                    menuTogglePasswordsVisibility?.title = getString(R.string.show_passwords)
                }
            }
        }

        Security.getUnlockStatus().observe(this) {
            updateMenuDelete(it)
            menuTogglePasswordsVisibility?.isVisible = it
            deletedPasswordsAdapter.updateUnlockStatus(it)
        }
    }

    private fun initRecycler() {
        deletedPasswordsAdapter.setOnRecyclerClickListener(object : OnRecyclerItemClick {
            override fun onItemClick(position: Int) {
                if (Security.isAppUnlocked()) {
                    when (isSelectingItems) {
                        true -> handleUserSelection(position)
                        else -> deletedPasswordsAdapter.unselectItemAtPosition(position)
                    }
                } else toastH.showToast(getString(R.string.unlock_first))
            }

            override fun onLongClick(position: Int) {
                if (Security.isAppUnlocked()) {
                    if (deletedPasswordsAdapter.isItemSelected(position)) {
                        val siteName = model.getDeletedPasswordsList().value!![position].siteName
                        toastH.showToast(getString(R.string.site_name_password, siteName))
                    } else {
                        deletedPasswordsAdapter.selectItemAtPosition(position)
                    }
                } else {
                    toastH.showToast(getString(R.string.unlock_first))
                }
            }
        })
        with(binding.recyclerDeletedPasswords) {
            layoutManager = LinearLayoutManager(
                this@DeletedPasswordsActivity,
                RecyclerView.VERTICAL,
                false
            )
            addItemDecoration(
                DividerItemDecoration(
                    this@DeletedPasswordsActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = deletedPasswordsAdapter
        }
    }

    fun handleUserSelection(position: Int) {
        when (deletedPasswordsAdapter.isItemSelected(position)) {
            true -> deletedPasswordsAdapter.unselectItemAtPosition(position)
            else -> {
                if (!isMaxCountSelected) deletedPasswordsAdapter.selectItemAtPosition(position)
                else (toastH.showToast(
                    getString(
                        R.string.maximum_selection_exceeded, AdapterDialogPasswords.MAX_ITEM_COUNT
                    )
                ))
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deleted_passwords, menu)
        menuRestorePasswords = menu?.findItem(R.id.menuRestorePasswords)
        menuDeletePasswords = menu?.findItem(R.id.menuDeletePermanently)
        menuUnlockApp = menu?.findItem(R.id.menuToggleDeletedLock)
        menuTogglePasswordsVisibility = menu?.findItem(R.id.menuToggleDeletedVisibility)

        if (!Security.isAppUnlocked()) menuTogglePasswordsVisibility?.isVisible = false

        updateMenuDelete(Security.isAppUnlocked())
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateMenuDelete(isUnlocked: Boolean) {
        when (isUnlocked) {
            true -> {
                menuUnlockApp?.setTitle(R.string.lock_app)
                menuUnlockApp?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            }
            else -> {
                menuUnlockApp?.setTitle(R.string.unlock_app)
                menuUnlockApp?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dialog = RestoreOrDeleteDialog(this)
        dialog.setPasswordList(deletedPasswordsAdapter.getSelectedPasswords())
        dialog.setOnActionConfirmationCallback(object : OnActionConfirmation {
            override fun onConfirmation() {
                deletedPasswordsAdapter.unselectAll()
            }
        })
        var dialogConfigured = false
        when (item.itemId) {
            R.id.menuRestorePasswords -> {
                dialog.setDialogType(RestoreOrDeleteDialog.RESTORE_PASSWORD)
                dialogConfigured = true
            }
            R.id.menuDeletePermanently -> {
                dialog.setDialogType(RestoreOrDeleteDialog.DELETE_PASSWORD)
                dialogConfigured = true
            }
            R.id.menuToggleDeletedLock -> {
                when (Security.isAppUnlocked()) {
                    true -> {
                        Security.lockApp()
                        deletedPasswordsAdapter.unselectAll()
                    }
                    else -> Security.unlockApp(this)
                }
            }
            R.id.menuToggleDeletedVisibility -> {
                when (Security.isAppUnlocked()) {
                    true -> deletedPasswordsAdapter.toggleVisibility()
                    else -> toastH.showToast(getString(R.string.unlock_first))
                }
            }
        }
        if (dialogConfigured) dialog.create().show()
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}
