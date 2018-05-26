package com.xema.cafemidas.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xema0 on 2018-05-26.
 */

public class Product {
    private int id;
    private long price;
    private String name;
    private int type;//category id
    private int orderId;
    @SerializedName("taking_time")
    private int takingTime;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTakingTime() {
        return takingTime;
    }

    public void setTakingTime(int takingTime) {
        this.takingTime = takingTime;
    }
}
