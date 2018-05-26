package com.xema.cafemidas.model;

import java.util.ArrayList;

public class OrderMenuList {
    private String id;
    private String pw;
    private String comment;
    private ArrayList<OrderItemList> item_list;

    public OrderMenuList(String id, String pw, String comment, ArrayList<OrderItemList> item_list) {
        this.id = id;
        this.pw = pw;
        this.comment = comment;
        this.item_list = item_list;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getComment() {
        return comment;
    }

    public ArrayList<OrderItemList> getItem_list() {
        return item_list;
    }
}
