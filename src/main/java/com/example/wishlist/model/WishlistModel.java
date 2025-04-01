package com.example.wishlist.model;

public class WishlistModel {
    private String name;
    private String description;

    public WishlistModel(String name, String description){
        this.name = name;
        this.description = description;
    }

    public WishlistModel(){
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


}
