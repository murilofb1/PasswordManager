package com.example.passwordgeneratorv2.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class WebsiteModel implements Serializable {
    protected String siteName;
    protected String iconLink;
    protected String siteLink;

    public WebsiteModel() {
    }

    public WebsiteModel(String siteName, String iconLink, String siteLink) {
        this.siteName = siteName;
        this.iconLink = iconLink;
        this.siteLink = siteLink;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    @Exclude
    public Uri iconLinkToUri() {
        return Uri.parse(iconLink);
    }
}
