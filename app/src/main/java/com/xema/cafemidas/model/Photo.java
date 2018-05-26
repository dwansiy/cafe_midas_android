package com.xema.cafemidas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xema0 on 2018-05-26.
 */

public class Photo {
    @SerializedName("photo_url")
    private String profileImage;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
