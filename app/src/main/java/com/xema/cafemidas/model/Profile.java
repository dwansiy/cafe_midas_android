package com.xema.cafemidas.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xema0 on 2018-05-26.
 */

public class Profile implements Parcelable{
    private int uid;
    private String name;
    @SerializedName("profile_image")
    private String profileImage;
    private int type;//슈퍼관리자 : 2, 서브관리자 : 1, 사용자 : 0
    private long point;
    private String comment;

    protected Profile(Parcel in) {
        uid = in.readInt();
        name = in.readString();
        profileImage = in.readString();
        type = in.readInt();
        point = in.readLong();
        comment = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(name);
        dest.writeString(profileImage);
        dest.writeInt(type);
        dest.writeLong(point);
        dest.writeString(comment);
    }
}
