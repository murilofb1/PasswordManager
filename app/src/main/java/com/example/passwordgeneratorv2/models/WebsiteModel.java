package com.example.passwordgeneratorv2.models;

public class WebsiteModel {
    private String name;
    private String iconLink;
    private String siteLink;
    private boolean beingUsed = false;

    public WebsiteModel() {
    }

    public WebsiteModel(String name, String iconLink, String siteLink) {
        this.name = name;
        this.iconLink = iconLink;
        this.siteLink = siteLink;
    }

    public String getName() {
        return name;
    }

    public String getIconLink() {
        return iconLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public boolean isBeingUsed() {
        return beingUsed;
    }

    public void setBeingUsed(boolean beingUsed) {
        beingUsed = beingUsed;
    }
}
