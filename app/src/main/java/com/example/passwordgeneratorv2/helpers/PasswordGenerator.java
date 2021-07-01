package com.example.passwordgeneratorv2.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {
    private String[] lowerCases = {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "l", "k", "j", "h", "g", "f", "d", "s", "a", "z", "x", "c", "v", "b", "n", "m"};
    private String[] upperCases = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "L", "K", "J", "H", "G", "F", "D", "S", "A", "Z", "X", "C", "V", "B", "N", "M"};
    private String[] specialChar = {"#", "$", "!", "@", "%", "&", "*", "_", "-", "+", ",", ".", "<", ">", ":", "?"};
    private String[] excludeSpecial = {};

    List<String> passwordItems = new ArrayList<>();


    public int passwordSize = 8;
    public int lowerCasesCount = 1;
    public int upperCasesCount = 1;
    public int specialCharCount = 2;
    public int numberCount = 4;
    public final int maxPasswordSize = 16;
    public final int minPasswordSize = 4;

    public PasswordGenerator() {
    }

    public PasswordGenerator(int passwordSize, int lowerCasesCount, int upperCasesCount, int specialCharCount) {
        int sum = lowerCasesCount + upperCasesCount + specialCharCount;
        this.passwordSize = passwordSize;
        this.lowerCasesCount = lowerCasesCount;
        this.upperCasesCount = upperCasesCount;
        this.specialCharCount = specialCharCount;
        this.numberCount = passwordSize - sum;
    }

    public String generatePassword() {
        generateChars();
        String finalPsswd = "";
        Collections.shuffle(passwordItems);
        for (String charItem : passwordItems) {
            finalPsswd += charItem;
        }

        return finalPsswd;
    }

    public String shufflePassword() {
        String finalPsswd = "";
        Collections.shuffle(passwordItems);
        for (String charItem : passwordItems) {
            finalPsswd += charItem;
        }
        return finalPsswd;
    }

    private void generateChars() {
        passwordItems.clear();

        for (int i = 0; i < lowerCasesCount; i++) {
            int rng = new Random().nextInt(lowerCases.length);
            passwordItems.add(lowerCases[rng]);
        }
        for (int i = 0; i < upperCasesCount; i++) {
            int rng = new Random().nextInt(upperCases.length);
            passwordItems.add(upperCases[rng]);
        }
        for (int i = 0; i < specialCharCount; i++) {
            int rng = new Random().nextInt(specialChar.length);
            passwordItems.add(specialChar[rng]);
        }
        for (int i = 0; i < numberCount; i++) {
            int rng = new Random().nextInt(10);
            passwordItems.add(String.valueOf(rng));
        }
    }


    private int getBiggerChar() {
        int i = lowerCasesCount;
        if (lowerCasesCount <= upperCasesCount) i = upperCasesCount;
        if (upperCasesCount <= specialCharCount) i = specialCharCount;
        if (specialCharCount <= numberCount) i = numberCount;
        return i;
    }
}

