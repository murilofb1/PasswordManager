package com.example.passwordgeneratorv2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R

import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.OnRecyclerItemClick
import com.example.passwordgeneratorv2.models.Password
import java.util.*

class NewDeletedPasswordsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var passwordList: MutableList<Password> = ArrayList()
    private var recyclerClick: OnRecyclerItemClick? = null

    fun updateList(passwords: MutableList<Password>) {
        passwordList = passwords
        notifyDataSetChanged()
    }

    fun setOnRecyclerClickListener(listener: OnRecyclerItemClick) {
        recyclerClick = listener
    }

    override fun getItemViewType(position: Int): Int {
        val password = passwordList[position]
        if (password.showMenu == SHOW_DELETE_MENU) {
            return SHOW_DELETE_MENU
        } else if (password.showMenu == SHOW_RESTORE_MENU) {
            return SHOW_RESTORE_MENU
        }
        return HIDE_MENU
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_deleted_passwords, parent, false)
        if (viewType == SHOW_DELETE_MENU) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_deleted_menu_delete, parent, false)
            return ConfirmDeleteViewHolder(view)
        } else if (viewType == SHOW_RESTORE_MENU) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_deleted_menu_restore, parent, false)
            return RestorePasswordViewHolder(view)
        }
        return DeletedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val password = passwordList[position]
        if (holder is DeletedViewHolder) {
            val newHolder = holder
            newHolder.textSiteName.text = password.siteName
            if (AdapterPasswords.isUnlocked()) {
                newHolder.textPassword.text = Base64H.decode(password.password)
            } else {
                val maskedpassword =
                    AdapterPasswords.maskedPassword(Base64H.decode(password.password))
                newHolder.textPassword.text = maskedpassword
            }
            Glide.with(holder.itemView)
                .load(password.iconLink)
                .placeholder(R.drawable.default_image)
                .into(newHolder.imgSiteIcon)
        } else if (holder is ConfirmDeleteViewHolder) {
            val newHolder = holder
        } else if (holder is RestorePasswordViewHolder) {
            val newHolder = holder
        }
    }

    override fun getItemCount(): Int {
        return passwordList.size
    }

    fun showMenu(type: Int, position: Int) {
        for (i in passwordList.indices) {
            passwordList[i].showMenu = HIDE_MENU
        }
        passwordList[position].showMenu = type
        notifyDataSetChanged()
    }

    val isShowMenu: Boolean
        get() {
            for (i in passwordList.indices) {
                if (passwordList[i].showMenu != HIDE_MENU) {
                    return true
                }
            }
            return false
        }

    fun closeMenu() {
        for (i in passwordList.indices) {
            passwordList[i].showMenu = HIDE_MENU
        }
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        passwordList.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItemAt(position: Int, password: Password) {
        passwordList.add(position, password)
        notifyDataSetChanged()
    }

    fun getPasswordAt(position: Int): Password {
        return passwordList[position]
    }

    inner class DeletedViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        var textSiteName: TextView = itemView.findViewById(R.id.txtRecyclerDeletedName)
        var textPassword: TextView = itemView.findViewById(R.id.txtRecyclerDeletedPassword)
        var imgSiteIcon: ImageView = itemView.findViewById(R.id.imgRecyclerDeletedIcon)
        var deletedPasswordLayout: RelativeLayout =
            itemView.findViewById(R.id.deletedPasswordLayout)

        override fun onClick(v: View?) {
            recyclerClick?.onItemClick(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            recyclerClick?.onLongClick(adapterPosition)
            return true
        }

    }

    inner class ConfirmDeleteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    inner class RestorePasswordViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

    companion object {
        var HIDE_MENU = 0
        var SHOW_DELETE_MENU = 1
        var SHOW_RESTORE_MENU = 2
    }
}