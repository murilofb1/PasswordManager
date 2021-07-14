package com.example.passwordgeneratorv2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.ListDialogPasswordsBinding
import com.example.passwordgeneratorv2.models.Password

class AdapterDialogPasswords(
    context: Context,
    layoutResource: Int,
    val passwordList: List<Password>
) :
    ArrayAdapter<Password>(context, layoutResource, passwordList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ListDialogPasswordsBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        binding.listSiteName.text = passwordList[position].siteName
        Glide.with(binding.root.context)
            .load(passwordList[position].iconLinkToUri())
            .placeholder(R.drawable.default_image)
            .into(binding.listSiteIcon)

        return binding.root

    }
}