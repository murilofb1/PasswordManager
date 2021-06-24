package com.example.passwordgeneratorv2.helpers;

import android.util.Base64;

public class Base64H {

    public static String encode(String originalText) {
        return Base64.encodeToString(originalText.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decode(String originalText) {
        return new String(Base64.decode(originalText, Base64.DEFAULT));
    }
}
