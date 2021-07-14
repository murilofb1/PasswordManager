package com.example.passwordgeneratorv2.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.RecyclerPasswordsBinding
import com.example.passwordgeneratorv2.helpers.*
import com.example.passwordgeneratorv2.models.Password
import java.lang.IllegalArgumentException

import kotlin.collections.ArrayList

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
        Log.i("HomeActivityLog", "RemovedItemId = $removedPosition")
        removedPassword = password
        Log.i("HomeActivityLog", "RemovedPasswordName = ${removedPassword!!.siteName}")

        passwordList.removeAt(removedPosition)
        notifyDataSetChanged()
    }

    fun restoreLastItem() {
        passwordList.add(removedPosition, removedPassword!!)
        notifyItemInserted(removedPosition)
    }

    private var vibrator: VibratorH? = null
    fun addVibrationEffect(context: Context) {
        vibrator = VibratorH(context)
    }

    private var toast: ToastH? = null
    fun addToastFeedBack(context: Context) {
        toast = ToastH(context)
    }


    private var recyclerItemClickListener: OnRecyclerItemClick? = null
    fun setOnRecyclerCLickListener(listener: OnRecyclerItemClick?) {
        recyclerItemClickListener = listener
    }

    fun updateUnlockStatus(status: Boolean) {
        this.isUnlocked = status
        if (!status) {
            toggleVisibility(false)
        }
    }

    fun toggleVisibility(value: Boolean?) {
        if (value == null) {
            if (isUnlocked) passwordVisibility.value = !passwordVisibility.value!!
        } else {
            passwordVisibility.value = value
        }
        notifyDataSetChanged()
    }


    private lateinit var binding: RecyclerPasswordsBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        binding = RecyclerPasswordsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewViewHolder(binding.root, recyclerItemClickListener)
    }

    override fun onBindViewHolder(holder: NewAdapterPasswords.NewViewHolder, position: Int) {

        if (passwordList.isNotEmpty()) {
            holder.setIsRecyclable(false)
            val currentSite = passwordList[position]
            val decodedPassword = Base64H.decode(currentSite.password)
            if (passwordVisibility.value!!) binding.txtPasswordRecycler.text = decodedPassword
            else binding.txtPasswordRecycler.text = StringUtil.applyPasswordMask(decodedPassword)


            binding.txtNameRecycler.text = currentSite.siteName

            Glide.with(holder.itemView)
                .load(currentSite.iconLinkToUri())
                .placeholder(R.drawable.default_image)
                .into(binding.imgIconRecycler)
            binding.imgBtnCopyPassword.setOnClickListener {
                val context = holder.itemView.context
                if (isUnlocked) {
                    ClipboardH.copyString(
                        decodedPassword, context
                    )
                    toast?.showToast(
                        context.getString(
                            R.string.toast_copy_password,
                            currentSite.siteName
                        )
                    )
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
        private val recyclerItemClick: OnRecyclerItemClick?
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {

        override fun onClick(v: View) {
            recyclerItemClick?.onItemClick(adapterPosition)
        }

        override fun onLongClick(v: View): Boolean {
            recyclerItemClick?.onLongClick(adapterPosition)
            return true
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }

}