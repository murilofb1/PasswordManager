package com.example.passwordgeneratorv2.models;

import androidx.annotation.Nullable;

import com.example.passwordgeneratorv2.firebase.FirebaseDB;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Password extends WebsiteModel implements Serializable {
    private String id;
    private String password;
    @Exclude
    long deletedTime = 0;
    @Exclude
    int showMenu = 0;

    public Password() {
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if (obj instanceof Password) {
            Password password = (Password) obj;
            return password.getId() == this.getId();
        } else {
            return false;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap();
        userMap.put("id", id);
        userMap.put("siteName", siteName);
        userMap.put("password", password);
        userMap.put("iconLink", iconLink);
        userMap.put("siteLink", siteLink);
        return userMap;
    }

    public Password(String siteName, String password, String iconLink, String siteLink) {
        if (id == null || id.isEmpty()) id = FirebaseDB.Companion.generatePushKey();
        this.siteName = siteName;
        this.password = password;
        this.iconLink = iconLink;
        this.siteLink = siteLink;
    }

    public Password(String siteName, String iconLink) {
        if (id == null || id.isEmpty()) id = FirebaseDB.Companion.generatePushKey();
        this.siteName = siteName;
        this.iconLink = iconLink;
    }

    public String getPassword() {
        return password;
    }

    @Exclude
    public long getDeletedTime() {
        return deletedTime;
    }

    @Exclude
    public int getShowMenu() {
        return showMenu;
    }

    public void setShowMenu(int showMenu) {
        this.showMenu = showMenu;
    }

    public String getId() {
        return id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
