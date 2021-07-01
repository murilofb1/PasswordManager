package com.example.passwordgeneratorv2.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.RecyclerPasswordsBinding
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.helpers.VibratorH
import com.example.passwordgeneratorv2.home.HomeActivity
import com.example.passwordgeneratorv2.models.Password
import java.util.*

class NewAdapterPasswords : RecyclerView.Adapter<NewAdapterPasswords.NewViewHolder>() {
    //private var visiblePassword: BooleanArray
    private var toast: Toast? = null
    private var isUnlocked = false
    private var passwordVisibility = false

    private var passwordList: List<Password> = ArrayList()
    fun updateList(list: List<Password>) {
        this.passwordList = list
        notifyDataSetChanged()
    }

    private var vibrator: VibratorH? = null
    private val vibrationEnabled = false
    fun addVibrationEffect(context: Context?) {
        vibrator = VibratorH(context!!)
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
            val currentSite = passwordList[position]
            binding.txtPasswordRecycler.text = Base64H.decode(currentSite!!.password)
            binding.txtNameRecycler.text = currentSite.site
            Glide.with(holder.itemView)
                .load(currentSite.iconLink)
                .placeholder(R.drawable.default_image)
                .into(binding.imgIconRecycler)

        }
    }

    private fun clickListenerHandler(
        context: Context,
        password: String?,
        position: Int?
    ): View.OnClickListener {
        return View.OnClickListener { v: View ->
            if (isUnlocked) {
                copyPassword(Base64H.decode(password), context)
                val actualPassword =
                    passwordList[position!!]
                if (vibrationEnabled) vibrator!!.shortVibration()
                if (toast != null) {
                    toast!!.cancel()
                }
                toast = Toast.makeText(
                    context,
                    "Your " + actualPassword!!.site + " password was copied to clipboard",
                    Toast.LENGTH_SHORT
                )
            }
            /*
              else if (v.getId() == id.checkfav) {

                  Password senha = passwordList.get(position);


                  FirebaseHelper.getUserPasswordsReference()
                          .child(senha.getSite())
                          .child("favorite")
                          .setValue(newValue);


              }*/
            else {
                HomeActivity.openBiometricAuth()
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
    var isUnlocked = false

    fun copyPassword(password: String?, context: Context) {
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val coppiedData = ClipData.newPlainText("password", password)
        manager.setPrimaryClip(coppiedData)
    }

    fun maskedPassword(password: String): String {
        var masked = ""
        for (i in 0 until password.length) {
            masked += "*"
        }
        return masked
    }
}


}