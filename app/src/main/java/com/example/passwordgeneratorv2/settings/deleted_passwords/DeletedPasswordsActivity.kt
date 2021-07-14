package com.example.passwordgeneratorv2.settings.deleted_passwords

import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.adapters.AdapterDeletedPasswords
import com.example.passwordgeneratorv2.adapters.NewDeletedPasswordsAdapter
import com.example.passwordgeneratorv2.databinding.ActivityDeletedPasswordsBinding
import com.example.passwordgeneratorv2.helpers.OnRecyclerItemClick

class DeletedPasswordsActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeletedPasswordsBinding
    private val model: NewDeletedViewModel by viewModels()
    private val deletedPasswordsAdapter = NewDeletedPasswordsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeletedPasswordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addObservers()
        initRecycler()
    }

    var menuItemTest: MenuItem? = null
    private fun addObservers() {
        model.getDeletedPasswordsList().observe(this) {
            deletedPasswordsAdapter.updateList(it)
        }
        model.selectedItemCount().observe(this) {
            menuItemTest?.isVisible = it > 0
        }
    }

    private fun initRecycler() {
        deletedPasswordsAdapter.setOnRecyclerClickListener(object : OnRecyclerItemClick {
            override fun onItemClick(position: Int) {
                model.unselectItemAtPosition(position)
            }

            override fun onLongClick(position: Int) {
                model.selectItemAtPosition(position)
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
        menuItemTest = menu!!.findItem(R.id.menuRestorePasswords)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }
}