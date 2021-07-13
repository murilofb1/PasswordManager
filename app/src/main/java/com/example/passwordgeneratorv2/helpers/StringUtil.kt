package com.example.passwordgeneratorv2.helpers

object StringUtil {

    fun applyPasswordMask(string: String): String {
        var masked = ""

        var i = 0
        while (i < string.length) {
            masked += "*"
            i++
        }

        return masked
    }
}