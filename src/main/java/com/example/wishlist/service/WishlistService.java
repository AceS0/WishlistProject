package com.example.wishlist.service;

import com.example.wishlist.model.Item;
import com.example.wishlist.model.User;
import com.example.wishlist.model.WishlistModel;
import com.example.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository){
        this.wishlistRepository = wishlistRepository;
    }

    public void addWish(WishlistModel wishlist, int userId){
        wishlistRepository.addWish(wishlist,userId);
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

    public List<Item> getWishItemsOfUser(String username, String wishlistname){
        int wishlistId = wishlistRepository.getWishlistIdByName(username,wishlistname);
        return wishlistRepository.getWishItemsOfUser(wishlistId);
    }

    public Item getSpecificWishItemOfUser(String username, String wishlistName, String itemName){
        List<Item> userItems = getWishItemsOfUser(username, wishlistName); // Get all items in the wishlist
        return userItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .orElse(null);
    }

    public void updateWish(WishlistModel updatedWish){
        wishlistRepository.updateWish(updatedWish);
    }

    public boolean deleteWish(String name){
        return wishlistRepository.deleteWish(name);
    }

    public String encodePassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean login(String uid, String pw){
        User user = wishlistRepository.getUser(uid);
        if (user != null){
            // Fandt bruger - tjekker om koden passer
            String storedHashedPassword = user.getPw();
            return checkPassword(pw, storedHashedPassword);
        }
        // Fandt ikke brugeren.
        return false;

    }

    public boolean checkPassword(String enteredPassword, String storedHashedPassword) {
        return BCrypt.checkpw(enteredPassword, storedHashedPassword);  // Verify the password
    }

    private boolean isValidUsername(String uid) {
        String regex = "^[a-zA-Z0-9_]{3,15}$";  //3-15 characters
        return uid.matches(regex);
    }

    public boolean register(String uid, String pw){
        User user = wishlistRepository.getUser(uid);
        String encodedPassword = encodePassword(pw);
        if (user != null) {
            return false;
        }
        if (!isValidUsername(uid)){
            return false;
        }
        wishlistRepository.registerUser(uid, encodedPassword);
        return true;
    }
}
