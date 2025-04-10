package com.example.wishlist.repository;

import com.example.wishlist.model.Item;
import com.example.wishlist.model.User;
import com.example.wishlist.model.WishlistModel;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marker klassen som en repository-komponent i Spring – bruges til databaseadgang
public class WishlistRepository {
    private final JdbcTemplate jdbcTemplate;

    // Constructor injection af JdbcTemplate, som bruges til at udføre SQL-spørgsmål
    public WishlistRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    // Henter en bruger fra databasen ud fra brugernavn
    public User getUser(String uid){
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            return jdbcTemplate.queryForObject(sql, mapUsers(), uid);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    // Registrerer en ny bruger i databasen
    public void registerUser(String uid, String pw){
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, uid, pw);
    }

    // Tilføjer en ny ønskeliste til databasen
    public void addWishList(WishlistModel wishlist, int userId){
        try {
            String sql = "INSERT INTO wishlists (user_id, name, description) VALUES (?,?,?)";
            jdbcTemplate.update(sql, userId, wishlist.getName(), wishlist.getDescription());
        } catch (DuplicateKeyException ignored){
        }
    }

    // Tilføjer et ønske til en ønskeliste
    public void addWish(Item wish, int listId){
        try {
            String sql = "INSERT INTO wishlist_items (wishlist_id, name, description) VALUES (?,?,?)";
            jdbcTemplate.update(sql, listId, wish.getName(), wish.getDescription());
        } catch (DuplicateKeyException ignored){
        }
    }

    // Henter en ønskeliste ud fra dens navn
    public WishlistModel getWishByName(String name){
        try {
            String sql = "SELECT * FROM wishlists WHERE name = ?";
            return jdbcTemplate.queryForObject(sql, mapWishlist(), name);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    // Henter brugerens ID ud fra brugernavn
    public int getUserIdByUsername(String username){
        String sql = "SELECT id FROM users WHERE username = ?";
        Integer userId = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return (userId != null) ? userId : -1;
    }

    // Henter alle ønskelister for en bestemt bruger
    public List<WishlistModel> getWishlistsByUserId(int userId) {
        String sql = "SELECT * FROM wishlists WHERE user_id = ?";
        return jdbcTemplate.query(sql, mapWishlist(), userId);
    }

    // Henter alle ønsker i en bestemt ønskeliste
    public List<Item> getWishItemsOfUser(int wishlistId){
        try {
            String sql = "SELECT wi.id AS item_id, wi.name AS item_name, wi.description AS item_description, wi.checked AS item_checked " +
                    "FROM wishlist_items wi WHERE wi.wishlist_id = ?";
            return jdbcTemplate.query(sql, mapWishItems(), wishlistId);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    // Henter ønskeliste-ID ud fra brugernavn og ønskelistenavn
    public int getWishlistIdByName(String username, String wishlistName) {
        try {
            String sql = "SELECT w.id FROM wishlists w " +
                    "JOIN users u ON w.user_id = u.id " +
                    "WHERE u.username = ? AND w.name = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, username, wishlistName);
        } catch (EmptyResultDataAccessException | NullPointerException e){
            return -1;
        }
    }

    // (Ubrugt metode, men defineret): Skulle hente ønske-ID ud fra navn – ikke færdiggjort
    public int getWishIdByName(int wishlistId, String wishName){
        String sql = "SELECT"; // Ikke implementeret
        return 1;
    }

    // Opdaterer en ønskeliste (navn og beskrivelse)
    public void updateWishList(WishlistModel updatedWishList){
        String sql = "UPDATE wishlists SET name = ?, description = ? WHERE id = ?";
        jdbcTemplate.update(sql, updatedWishList.getName(), updatedWishList.getDescription(), updatedWishList.getId());
    }

    // Opdaterer et ønske (navn og beskrivelse) i en ønskeliste
    public void updateWish(Item updatedWish, int wishId) {
        String sql = "UPDATE wishlist_items SET name = ?, description = ? WHERE wishlist_id = ? AND id = ?";
        jdbcTemplate.update(sql, updatedWish.getName(), updatedWish.getDescription(), updatedWish.getId(), wishId);
    }

    // Sletter en ønskeliste ud fra ID og navn
    public boolean deleteWishlist(int listId, String name){
        String sql = "DELETE FROM wishlists WHERE id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql, listId, name);
        return rowsAffected > 0;
    }

    // Sletter et ønske fra ønskelisten ud fra navn
    public boolean deleteWish(int listId, String wishname){
        String sql = "DELETE FROM wishlist_items WHERE wishlist_id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql, listId, wishname);
        return rowsAffected > 0;
    }

    // Opdaterer "checked"-status for et ønske
    public boolean updateWishItemChecked(boolean checked, int listId, String wishname, int wishId){
        String sql = "UPDATE wishlist_items SET checked = ? WHERE wishlist_id = ? AND name = ? AND id = ?";
        int rowsAffected = jdbcTemplate.update(sql, checked, listId, wishname, wishId);
        return rowsAffected > 0;
    }

    // Mapper en række fra "wishlists"-tabellen til et WishlistModel-objekt
    private RowMapper<WishlistModel> mapWishlist(){
        return (rs, rowNum) -> new WishlistModel(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }

    // Mapper en række fra "wishlist_items"-tabellen til et Item-objekt
    private RowMapper<Item> mapWishItems(){
        return (rs, rowNum) -> new Item(
                rs.getInt("item_id"),
                rs.getString("item_name"),
                rs.getString("item_description"),
                rs.getBoolean("item_checked")
        );
    }

    // Mapper en række fra "users"-tabellen til et User-objekt
    private RowMapper<User> mapUsers(){
        return (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
