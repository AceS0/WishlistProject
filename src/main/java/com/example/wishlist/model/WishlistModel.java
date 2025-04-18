package com.example.wishlist.model;

import java.util.List;

public class WishlistModel {
    private int id;
    private String name;
    private String description;
    private List<Item> items;

    public WishlistModel(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public WishlistModel(){
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
}
