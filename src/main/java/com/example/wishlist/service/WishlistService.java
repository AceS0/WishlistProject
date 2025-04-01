package com.example.wishlist.service;

import com.example.wishlist.model.User;
import com.example.wishlist.model.WishlistModel;
import com.example.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository){
        this.wishlistRepository = wishlistRepository;
    }

    public void addWish(WishlistModel wish, int userId){
        wishlistRepository.addWish(wish,userId);
    }


    public List<WishlistModel> getAllWishes(){
        return wishlistRepository.getAllWishes();
    }

    public WishlistModel getWishByName(String name){
        return wishlistRepository.getWishByName(name);
    }

    public int getUserIdByUsername(String username){
        return wishlistRepository.getUserIdByUsername(username);
    }

    public List<WishlistModel> getWishlistsByUserId(int userId) {
        return wishlistRepository.getWishlistsByUserId(userId);
    }

    public void updateWish(WishlistModel updatedWish){
        wishlistRepository.updateWish(updatedWish);
    }

    public boolean deleteWish(String name){
        return wishlistRepository.deleteWish(name);
    }

    public boolean login(String uid, String pw){
        User user = wishlistRepository.getUser(uid);
        if (user != null){
            // Fandt bruger - tjekker om koden passer
            return user.getPw().equals(pw);
        }
        // Fandt ikke brugeren.
        return false;

    }
}
