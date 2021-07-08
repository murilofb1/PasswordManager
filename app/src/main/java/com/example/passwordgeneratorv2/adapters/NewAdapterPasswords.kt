package com.example.passwordgeneratorv2.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.RecyclerPasswordsBinding
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.ClipboardH
import com.example.passwordgeneratorv2.helpers.ToastH
import com.example.passwordgeneratorv2.helpers.VibratorH
import com.example.passwordgeneratorv2.home.HomeActivity
import com.example.passwordgeneratorv2.models.Password
import java.util.*

class NewAdapterPasswords : RecyclerView.Adapter<NewAdapterPasswords.NewViewHolder>() {

    private var isUnlocked = false
    private var passwordVisibility = false

    private var passwordList: List<Password> = ArrayList()
    fun updateList(list: List<Password>) {
        this.passwordList = list
        notifyDataSetChanged()
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
            passwordVisibility = false
        }
    }

    fun toggleVisibility() {

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
            binding.txtPasswordRecycler.text = Base64H.decode(currentSite!!.password)
            binding.txtNameRecycler.text = currentSite!!.siteName

            Glide.with(holder.itemView)
                .load(currentSite.iconLinkToUri())
                .placeholder(R.drawable.default_image)
                .into(binding.imgIconRecycler)
            binding.imgBtnCopyPassword.setOnClickListener {
                val context = holder.itemView.context
                if (isUnlocked) {
                    ClipboardH.copyString(
                        Base64H.decode(currentSite!!.password), context
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


    override fun getItemCount(): Int = passwordList.size

    inner class NewViewHolder(itemView: View, private val recyclerItemClick: OnRecyclerItemClick?) :
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

    interface OnRecyclerItemClick {
        fun onItemClick(position: Int)
        fun onLongClick(position: Int)
    }

    companion object {

        fun maskedPassword(password: String): String {
            var masked = ""
            for (i in 0 until password.length) {
                masked += "*"
            }
            return masked
        }
    }


}