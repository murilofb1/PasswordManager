package com.example.passwordgeneratorv2.helpers;

public class ArrayHelper {
    public static boolean[] setAll(boolean[] booleanArray, boolean bol) {
        boolean[] newBool = new boolean[booleanArray.length];
        for (int i = 0; i < booleanArray.length; i++) {
            newBool[i] = bol;
        }
        return newBool;
    }
}
