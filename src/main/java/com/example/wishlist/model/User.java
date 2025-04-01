package com.example.wishlist.model;

public class User {
    private String uid;
    private String pw;

    public User(String uid, String pw){
        this.uid = uid;
        this.pw = pw;
    }

    public User(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getPw(){
        return pw;
    }

    public void setPw(String pw){
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "Users{" +
                "uid='" + uid + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}
