package com.xema.cafemidas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xema0 on 2018-05-26.
 */

public class Profile {
    private int uid;
    private String name;
    @SerializedName("photo_url")
    private String profileImage;
    private int type;//슈퍼관리자 : 2, 서브관리자 : 1, 사용자 : 0
    private long point;
    private String comment;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
