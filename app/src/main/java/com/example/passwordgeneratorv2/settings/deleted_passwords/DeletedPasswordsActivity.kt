package com.example.passwordgeneratorv2.settings.deleted_passwords

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordgeneratorv2.CustomDialogs
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.adapters.NewDeletedPasswordsAdapter
import com.example.passwordgeneratorv2.databinding.ActivityDeletedPasswordsBinding
import com.example.passwordgeneratorv2.helpers.OnRecyclerItemClick
import com.example.passwordgeneratorv2.helpers.ToastH

class DeletedPasswordsActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeletedPasswordsBinding
    private val model: NewDeletedViewModel by viewModels()
    private val deletedPasswordsAdapter = NewDeletedPasswordsAdapter()

    private var menuRestorePasswords: MenuItem? = null
    private var menuDeletePasswords: MenuItem? = null

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
    private fun addObservers() {
        model.getDeletedPasswordsList().observe(this) {
            deletedPasswordsAdapter.updateList(it)
        }
        deletedPasswordsAdapter.getSelectedItemCount().observe(this) {
            menuRestorePasswords?.isVisible = it > 0
            menuDeletePasswords?.isVisible = it > 0
            isSelectingItems = it > 0
        }
    }

    private fun initRecycler() {
        deletedPasswordsAdapter.setOnRecyclerClickListener(object : OnRecyclerItemClick {
            override fun onItemClick(position: Int) {
                //model.unselectItemAtPosition(position)
                if (isSelectingItems) {
                    if (deletedPasswordsAdapter.isItemSelected(position)) {
                        deletedPasswordsAdapter.unselectItemAtPosition(position)
                    } else {
                        deletedPasswordsAdapter.selectItemAtPosition(position)
                    }

                } else {
                    deletedPasswordsAdapter.unselectItemAtPosition(position)
                }
            }

            override fun onLongClick(position: Int) {
                if (deletedPasswordsAdapter.isItemSelected(position)) {
                    val siteName = model.getDeletedPasswordsList().value!![position].siteName
                    toastH.showToast(getString(R.string.site_name_password, siteName))
                } else {
                    deletedPasswordsAdapter.selectItemAtPosition(position)
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_deleted_passwords, menu)
        menuRestorePasswords = menu?.findItem(R.id.menuRestorePasswords)
        menuDeletePasswords = menu?.findItem(R.id.menuDeletePermanently)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuRestorePasswords -> {

            }
            R.id.menuDeletePermanently -> {

                CustomDialogs(this)
                    .setPasswordList(deletedPasswordsAdapter.getSelectedPasswords())
                    .activateToastMessages()
                    .setDialogType(CustomDialogs.DELETE_PASSWORDS)
                    .showDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}