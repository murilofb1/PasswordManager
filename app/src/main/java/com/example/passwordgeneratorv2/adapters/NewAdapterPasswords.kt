package com.example.passwordgeneratorv2.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.RecyclerPasswordsBinding
import com.example.passwordgeneratorv2.helpers.*
import com.example.passwordgeneratorv2.models.Password
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import java.lang.IllegalArgumentException

import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

class NewAdapterPasswords : RecyclerView.Adapter<NewAdapterPasswords.NewViewHolder>() {

    private var isUnlocked = false
    private val passwordVisibility = MutableLiveData(false)
    fun isPasswordsVisible(): LiveData<Boolean> = passwordVisibility


    private var passwordList: MutableList<Password> = ArrayList()

    fun updateList(list: MutableList<Password>) {
        this.passwordList = list
        notifyDataSetChanged()
    }

    private var removedPosition = 0
    var removedPassword: Password? = null

    fun removeItem(password: Password) {
        removedPosition = getItemPosition(password)
        removedPassword = password
        passwordList.removeAt(removedPosition)
        notifyItemRemoved(removedPosition)
        notifyItemRangeChanged(removedPosition, passwordList.size)
    }

    fun restoreLastItem() {
        if (removedPassword!= null){
            passwordList.add(removedPosition, removedPassword!!)
            notifyItemInserted(removedPosition)
            notifyItemRangeChanged(removedPosition, passwordList.size)
        }
    }

    private var vibrator: VibratorH? = null
    fun addVibrationEffect(context: Context) { vibrator = VibratorH(context) }

    private var toast: ToastH? = null
    fun addToastFeedBack(context: Context) { toast = ToastH(context) }


    private var recyclerItemClickListener: OnRecyclerItemClick? = null
    fun setOnRecyclerCLickListener(listener: OnRecyclerItemClick?) {
        recyclerItemClickListener = listener
    }

    fun updateUnlockStatus(status: Boolean) {
        this.isUnlocked = status
        if (!status) toggleVisibility(false)
    }

    fun toggleVisibility(value: Boolean?) {
        when (value) {
            null -> if (isUnlocked) passwordVisibility.value = !passwordVisibility.value!!
            else -> passwordVisibility.value = value
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_passwords, parent, false)

        return NewViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewAdapterPasswords.NewViewHolder, position: Int) {

        if (passwordList.isNotEmpty()) {

            val currentSite = passwordList[position]
            val decodedPassword = Base64H.decode(currentSite.password)
            if (passwordVisibility.value!!) holder.txtPassword.text = decodedPassword
            else holder.txtPassword.text = StringUtil.applyPasswordMask(decodedPassword)

            holder.txtPasswordName.text = currentSite.siteName

            Glide.with(holder.itemView)
                .load(currentSite.iconLinkToUri())
                .placeholder(R.drawable.default_image)
                .into(holder.imgPasswordIcon)

            holder.btnCopyPassword.setOnClickListener {
                val context = holder.itemView.context
                if (isUnlocked) {
                    ClipboardH.copyString(decodedPassword, context)
                    toast?.showToast(context.getString(
                            R.string.toast_copy_password,
                            currentSite.siteName
                        ))
                    vibrator?.shortVibration()
                } else {
                    toast?.showToast(holder.itemView.context.getString(R.string.unlock_first))
                }
            }
        }
    }

    private fun getItemPosition(password: Password): Int {
        var i = 0
        while (i < passwordList.size) {
            val listItem = passwordList[i]
            if (listItem == password) return i
            i++
        }
        throw IllegalArgumentException("The item doesn't exists in the list")
    }

    override fun getItemCount(): Int = passwordList.size

    inner class NewViewHolder(
        itemView: View,
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
        val imgPasswordIcon: ShapeableImageView = itemView.findViewById(R.id.imgRecyclerHomeIcon)
        val txtPasswordName: MaterialTextView = itemView.findViewById(R.id.txtRecyclerHomeName)
        val txtPassword: MaterialTextView = itemView.findViewById(R.id.txtRecyclerHomePassword)
        val btnCopyPassword: ImageButton = itemView.findViewById(R.id.imgBtnCopyPassword)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View) { recyclerItemClickListener?.onItemClick(adapterPosition) }

        override fun onLongClick(v: View): Boolean {
            recyclerItemClickListener?.onLongClick(adapterPosition)
            return true
        }


    }

}