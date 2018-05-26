package com.xema.cafemidas.model;

public class OrderItemList {
    private int menu_id;
    private int cnt;

    public OrderItemList(int menu_id, int cnt) {
        this.menu_id = menu_id;
        this.cnt = cnt;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public int getCnt() {
        return cnt;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}
