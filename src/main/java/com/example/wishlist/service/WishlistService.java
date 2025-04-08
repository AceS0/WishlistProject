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

    public void addWishList(WishlistModel wishlist, int userId){
        wishlistRepository.addWishList(wishlist,userId);
    }

    public void addWish(Item wish, int listId){
        wishlistRepository.addWish(wish,listId);
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

    public List<Item> getWishItemsOfUser(int id){
        return wishlistRepository.getWishItemsOfUser(id);
    }

    public Item getSpecificWishItemOfUser(int id, String itemName){
        List<Item> userItems = getWishItemsOfUser(id); // Get all items in the wishlist
        return userItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .orElse(null);
    }

    public void updateWishList(WishlistModel updatedWishList){
        wishlistRepository.updateWishList(updatedWishList);
    }

    public void updateWish(Item updatedWish){
        wishlistRepository.updateWish(updatedWish);
    }

    public boolean updateWishItemChecked(boolean checked, String username, String wishname, String wishlistname){
        int wishlistId = wishlistRepository.getWishlistIdByName(username,wishlistname);
        return wishlistRepository.updateWishItemChecked(checked,wishlistId,wishname);
    }

    public boolean deleteWishlist(int userId, String name){
        return wishlistRepository.deleteWishlist(userId,name);
    }

    public boolean deleteWish(int listId, String wishname){
        return wishlistRepository.deleteWish(listId,wishname);
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
