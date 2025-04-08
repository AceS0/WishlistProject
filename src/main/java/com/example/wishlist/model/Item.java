package com.example.wishlist.model;


public class Item {
    private int id;
    private String name;
    private String description;
    private boolean checked;


    public Item(int id, String name, String description, boolean checked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.checked = checked;
    }


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "WishlistItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                '}';
    }
}
