package com.example.passwordgeneratorv2.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class ClipboardH {
    companion object {
        fun copyString(string: String, context: Context) {
            val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val copiedData = ClipData.newPlainText("password", string)
            manager.setPrimaryClip(copiedData)
        }
    }
}