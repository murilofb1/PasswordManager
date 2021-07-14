package com.example.passwordgeneratorv2.models;

import java.util.HashMap;

public class UserModel {
    private String name;
    private String email;

    public UserModel() {

    }

    public UserModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        return map;
    }

}
