package com.xema.cafemidas.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xema0 on 2018-05-26.
 */

public class Category implements Parent<Product> {
    private int id;
    private String name;
    // TODO: 2018-05-26
    private List<Product> productList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Product> getChildList() {
        return productList != null ? productList : new ArrayList<>();
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
