package com.example.passwordgeneratorv2.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.example.passwordgeneratorv2.R
import com.example.passwordgeneratorv2.databinding.ListDialogPasswordsBinding
import com.example.passwordgeneratorv2.helpers.Base64H
import com.example.passwordgeneratorv2.models.Password
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import java.security.InvalidParameterException

class AdapterDialogPasswords(
    context: Context,
    layoutResource: Int,
    val passwordList: List<Password>
) :
    ArrayAdapter<Password>(context, layoutResource, passwordList) {
    var visiblePasswords = Array(passwordList.size) { false }

    companion object {
        const val MAX_ITEM_COUNT = 8
    }

    override fun getCount(): Int {
        return if (passwordList.size <= MAX_ITEM_COUNT) passwordList.size
        else throw InvalidParameterException(
            "The list must be lower or equal than the max item count from the adapter"
        )

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.list_dialog_passwords, parent, false)

        val toggleVisibility: ImageButton = view.findViewById(R.id.btnToggleVisibility)
        val siteName: MaterialTextView = view.findViewById(R.id.listSiteName)
        val siteIcon: ShapeableImageView = view.findViewById(R.id.listSiteIcon)
        if (visiblePasswords[position]) {
            siteName.text = Base64H.decode(passwordList[position].password)
        } else {
            siteName.text = passwordList[position].siteName
        }


        Glide.with(view.context)
            .load(passwordList[position].iconLinkToUri())
            .placeholder(R.drawable.default_image)
            .into(siteIcon)

        toggleVisibility.setOnClickListener {
            toggleVisibilityFrom(position)
        }

        return view
    }

    private fun toggleVisibilityFrom(position: Int) {
        when (visiblePasswords[position]) {
            true -> visiblePasswords[position] = false
            else -> visiblePasswords[position] = true
        }
        notifyDataSetChanged()
    }

}


