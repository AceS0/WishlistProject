package com.example.wishlist.service;

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

    public void addWish(WishlistModel wish){
        wishlistRepository.addWish(wish);
    }

    public List<WishlistModel> getAllWishes(){
        return wishlistRepository.getAllWishes();
    }

    public WishlistModel getWishByName(String name){
        return wishlistRepository.getWishByName(name);
    }

    public boolean updateWish(WishlistModel updatedWish){
        return wishlistRepository.updateWish(updatedWish);
    }

    public boolean deleteWish(String name){
        return wishlistRepository.deleteWish(name);
    }
}
