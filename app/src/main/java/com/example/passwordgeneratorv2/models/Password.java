package com.example.passwordgeneratorv2.models;


import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Password implements Serializable {
    private String site;
    private String password;
    private String iconLink;
    private String siteLink;
    private boolean favorite = false;
    @Exclude
    long deletedTime = 0;
    @Exclude
    int showMenu = 0;

    public Password() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap();
        userMap.put("site", site);
        userMap.put("password", site);
        userMap.put("iconLink", site);
        userMap.put("siteLink", site);
        userMap.put("favorite", false);
        return userMap;
    }

    public Password(String site, String password, String iconLink, String siteLink) {
        this.site = site;
        this.password = password;
        this.iconLink = iconLink;
        this.siteLink = siteLink;
    }

    public Password(String site, String iconLink) {
        this.site = site;
        this.iconLink = iconLink;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public String getIconLink() {
        return iconLink;
    }

    public String getPassword() {
        return password;
    }

    public String getSite() {
        return site;
    }

    public long getDeletedTime() {
        return deletedTime;
    }

    public int getShowMenu() {
        return showMenu;
    }

    public void setShowMenu(int showMenu) {
        this.showMenu = showMenu;
    }
}
