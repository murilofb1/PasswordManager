package com.example.passwordgeneratorv2.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;

public class WebsiteModel {
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

    public String getSiteName() {
        return siteName;
    }

    public String getIconLink() {
        return iconLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    @Exclude
    public Uri iconLinkToUri() {
        return Uri.parse(iconLink);
    }
}
