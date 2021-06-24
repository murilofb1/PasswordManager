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


    boolean verifyInstance = false;

    List<String> passwordItems = new ArrayList<>();


    public int passwordSize = 8;
    public int lowerCaseSize = 1;
    public int upperCaseSize = 1;
    public int specialCharSize = 2;
    public int numberSize = 4;


    public PasswordGenerator() {

    }

    public PasswordGenerator(int passwordSize, int lowerCaseSize, int upperCaseSize, int specialCharSize) {
        int sum = lowerCaseSize + upperCaseSize + specialCharSize;
        this.passwordSize = passwordSize;
        this.lowerCaseSize = lowerCaseSize;
        this.upperCaseSize = upperCaseSize;
        this.specialCharSize = specialCharSize;
        this.numberSize = passwordSize - sum;
        this.verifyInstance = true;
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

        for (int i = 0; i < lowerCaseSize; i++) {
            int rng = new Random().nextInt(lowerCases.length);
            passwordItems.add(lowerCases[rng]);
        }
        for (int i = 0; i < upperCaseSize; i++) {
            int rng = new Random().nextInt(upperCases.length);
            passwordItems.add(upperCases[rng]);
        }
        for (int i = 0; i < specialCharSize; i++) {
            int rng = new Random().nextInt(specialChar.length);
            passwordItems.add(specialChar[rng]);
        }
        for (int i = 0; i < numberSize; i++) {
            int rng = new Random().nextInt(10);
            passwordItems.add(String.valueOf(rng));
        }

    }


    private int getBiggerChar() {
        int i = lowerCaseSize;

        if (lowerCaseSize <= upperCaseSize) {
            i = upperCaseSize;
        }
        if (upperCaseSize <= specialCharSize) {
            i = specialCharSize;
        }
        if (specialCharSize <= numberSize) {
            i = numberSize;
        }


        return i;
    }
}

