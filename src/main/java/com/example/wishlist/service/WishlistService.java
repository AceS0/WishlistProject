package com.example.wishlist.service;

import com.example.wishlist.model.Item;
import com.example.wishlist.model.User;
import com.example.wishlist.model.WishlistModel;
import com.example.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@Service // Gør klassen til en Spring Service – bruges til forretningslogik
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    // Constructor injection af repository – giver adgang til databasefunktioner
    public WishlistService(WishlistRepository wishlistRepository){
        this.wishlistRepository = wishlistRepository;
    }

    // Tilføjer en ønskeliste til en bruger
    public void addWishList(WishlistModel wishlist, int userId){
        wishlistRepository.addWishList(wishlist,userId);
    }

    // Tilføjer et ønske til en ønskeliste
    public void addWish(Item wish, int listId){
        wishlistRepository.addWish(wish,listId);
    }

    // Henter en ønskeliste ud fra dens navn
    public WishlistModel getWishByName(String name){
        return wishlistRepository.getWishByName(name);
    }

    // Henter brugerens ID ud fra brugernavn
    public int getUserIdByUsername(String username){
        return wishlistRepository.getUserIdByUsername(username);
    }

    // Henter alle ønskelister for en bruger
    public List<WishlistModel> getWishlistsByUserId(int userId) {
        return wishlistRepository.getWishlistsByUserId(userId);
    }

    // Henter alle ønsker i en ønskeliste (ud fra ID)
    public List<Item> getWishItemsOfUser(int id){
        return wishlistRepository.getWishItemsOfUser(id);
    }

    // Finder et specifikt ønske i en ønskeliste ud fra navn og ID
    public Item getSpecificWishItemOfUser(int id, String itemName, int wishId){
        List<Item> userItems = getWishItemsOfUser(id);
        return userItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName) && item.getId() == wishId)
                .findFirst()
                .orElse(null);
    }

    // Opdaterer en ønskeliste (navn og beskrivelse)
    public void updateWishList(WishlistModel updatedWishList){
        wishlistRepository.updateWishList(updatedWishList);
    }

    // Opdaterer et ønske i ønskelisten
    public void updateWish(Item updatedWish, int wishId){
        wishlistRepository.updateWish(updatedWish,wishId);
    }

    // Opdaterer om et ønske er "checked" eller ej
    public boolean updateWishItemChecked(boolean checked, int wishlistId, String wishname, int wishId){
        System.out.println(checked);
        return wishlistRepository.updateWishItemChecked(checked,wishlistId,wishname, wishId);
    }

    // Sletter en ønskeliste
    public boolean deleteWishlist(int listId, String name){
        return wishlistRepository.deleteWishlist(listId,name);
    }

    // Sletter et ønske fra en ønskeliste
    public boolean deleteWish(int listId, String wishname){
        return wishlistRepository.deleteWish(listId,wishname);
    }

    // Hasher adgangskoder med BCrypt, så de er sikre i databasen
    public String encodePassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Logger en bruger ind ved at tjekke brugernavn og kodeord
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

    // Sammenligner indtastet kode med hash fra databasen
    public boolean checkPassword(String enteredPassword, String storedHashedPassword) {
        return BCrypt.checkpw(enteredPassword, storedHashedPassword);  // Verify the password
    }

    // Tjekker om brugernavn er gyldigt (3-15 tegn, kun bogstaver, tal og _)
    private boolean isValidUsername(String uid) {
        String regex = "^[a-zA-Z0-9_]{3,15}$";  //3-15 characters
        return uid.matches(regex);
    }

    // Registrerer ny bruger hvis brugernavnet ikke findes og er gyldigt
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
