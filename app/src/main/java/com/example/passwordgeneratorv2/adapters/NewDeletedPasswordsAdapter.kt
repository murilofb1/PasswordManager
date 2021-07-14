package com.example.passwordgeneratorv2.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.RecyclerDeletedPasswordsBinding

import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.OnRecyclerItemClick
import com.example.passwordgeneratorv2.models.Password
import com.example.passwordgeneratorv2.to_delete.AdapterPasswords
import java.util.*
import kotlin.collections.ArrayList

class NewDeletedPasswordsAdapter :
    RecyclerView.Adapter<NewDeletedPasswordsAdapter.DeletedViewHolder>() {

    private var passwordList: MutableList<Password> = ArrayList()
    fun updateList(passwords: MutableList<Password>) {
        passwordList = passwords
        selectedItems = Array(passwords.size) { false }
        notifyDataSetChanged()
    }

    private var recyclerClick: OnRecyclerItemClick? = null
    fun setOnRecyclerClickListener(listener: OnRecyclerItemClick) {
        recyclerClick = listener
    }

    //Selection Control
    private var selectedItems: Array<Boolean> = emptyArray()
    private val selectedItemCount = MutableLiveData(0)
    fun getSelectedItemCount(): LiveData<Int> = selectedItemCount

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewDeletedPasswordsAdapter.DeletedViewHolder {
        val binding = RecyclerDeletedPasswordsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DeletedViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: NewDeletedPasswordsAdapter.DeletedViewHolder,
        position: Int
    ) {
        val password = passwordList[position]


        if (isItemSelected(position)) {
            holder.deletedPasswordLayout.setBackgroundResource(R.color.rippleColor)
        } else {
            holder.deletedPasswordLayout.setBackgroundResource(android.R.color.white)
        }

        holder.textSiteName.text = password.siteName
        if (AdapterPasswords.isUnlocked()) {
            holder.textPassword.text = Base64H.decode(password.password)
        } else {
            val maskedpassword =
                AdapterPasswords.maskedPassword(Base64H.decode(password.password))
            holder.textPassword.text = maskedpassword
        }
        Glide.with(holder.itemView)
            .load(password.iconLink)
            .placeholder(R.drawable.default_image)
            .into(holder.imgSiteIcon)

    }

    override fun getItemCount(): Int = passwordList.size


    fun selectItemAtPosition(position: Int) {
        if (!selectedItems[position]) {
            selectedItems[position] = true
            selectedItemCount.value.let {
                selectedItemCount.value = it!! + 1
            }
            Log.i("DeletedActivityLog", "${passwordList[position].siteName} Selected")
            notifyItemChanged(position)
        }
    }

    fun unselectItemAtPosition(position: Int) {
        if (selectedItems[position]) {
            selectedItems[position] = false
            selectedItemCount.value.let {
                selectedItemCount.value = it!! - 1
            }
            Log.i("DeletedActivityLog", "${passwordList[position].siteName} Unselected")
            notifyItemChanged(position)
        }
    }

    fun isItemSelected(itemPosition: Int): Boolean = selectedItems[itemPosition]


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

    fun getSelectedPasswords(): List<Password> {
        var i = 0
        val list = ArrayList<Password>()
        while (i < passwordList.size) {
            if (isItemSelected(i)) list.add(passwordList[i])
            i++
        }
        return list
    }

    inner class DeletedViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        var textSiteName: TextView = itemView.findViewById(R.id.txtRecyclerDeletedName)
        var textPassword: TextView = itemView.findViewById(R.id.txtRecyclerDeletedPassword)
        var imgSiteIcon: ImageView = itemView.findViewById(R.id.imgRecyclerDeletedIcon)
        var deletedPasswordLayout: RelativeLayout =
            itemView.findViewById(R.id.deletedPasswordLayout)

        init {
            deletedPasswordLayout.setOnClickListener(this)
            deletedPasswordLayout.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            recyclerClick?.onItemClick(adapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            recyclerClick?.onLongClick(adapterPosition)
            return true
        }

    }


}