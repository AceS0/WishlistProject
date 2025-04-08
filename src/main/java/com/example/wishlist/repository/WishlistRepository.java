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

@Repository
public class WishlistRepository {
    private final JdbcTemplate jdbcTemplate;

    public WishlistRepository(JdbcTemplate jdbcTemplate){
    this.jdbcTemplate = jdbcTemplate;
    }

    public User getUser(String uid){
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            return jdbcTemplate.queryForObject(sql, mapUsers(), uid);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void registerUser(String uid, String pw){
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql,uid,pw);
    }

    //Tilføjer et ønske til databasen. (Create funktion)
    public void addWishList(WishlistModel wishlist, int userId){
        try {

            String sql = "INSERT INTO wishlists (user_id,name, description) VALUES (?,?,?)";
            jdbcTemplate.update(sql,userId,wishlist.getName(),wishlist.getDescription());

        } catch (DuplicateKeyException ignored){
        }
    }

    public void addWish(Item wish, int listId){
        try {
            String sql = "INSERT INTO wishlist_items (wishlist_id, name, description) VALUES (?,?,?)";
            System.out.println("Navn: " +wish.getName() + "Desc: " + wish.getDescription());
            jdbcTemplate.update(sql,listId,wish.getName(),wish.getDescription());
        } catch (DuplicateKeyException ignored){
        }
    }

    //Henter et ønske ud fra navnet. (Read funktion)
    public WishlistModel getWishByName(String name){
        try {
            String sql = "SELECT * FROM wishlists WHERE name = ?";
            return jdbcTemplate.queryForObject(sql, mapWishlist(),name);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public int getUserIdByUsername(String username){
        String sql = "SELECT id FROM users WHERE username = ?";
        Integer userId = jdbcTemplate.queryForObject(sql,Integer.class,username);
        return (userId != null) ? userId : -1;
    }

    public List<WishlistModel> getWishlistsByUserId(int userId) {
        String sql = "SELECT * FROM wishlists WHERE user_id = ?";
        return jdbcTemplate.query(sql, mapWishlist(), userId);
    }

    public List<Item> getWishItemsOfUser(int wishlistId){
        try {
            String sql = "SELECT wi.id AS item_id, wi.name AS item_name, wi.description AS item_description, wi.checked AS item_checked " +
                    "FROM wishlist_items wi WHERE wi.wishlist_id = ?";
        return jdbcTemplate.query(sql, mapWishItems(),wishlistId);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public int getWishlistIdByName(String username, String wishlistName) {
        try {
        String sql = "SELECT w.id FROM wishlists w " +
                "JOIN users u ON w.user_id = u.id " +
                "WHERE u.username = ? AND w.name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username, wishlistName);
        } catch (EmptyResultDataAccessException e){
            return -1;
        }
    }

    //Opdaterer et ønske, hvis der opstår en ændring. (Update funktion)
    public void updateWishList(WishlistModel updatedWishList){
        String sql = "UPDATE wishlists SET name = ?, description = ? WHERE id = ?";
        jdbcTemplate.update(sql,updatedWishList.getName(),updatedWishList.getDescription(),updatedWishList.getId());
    }

    public void updateWish(Item updatedWish){
        String sql = "UPDATE wishlist_items SET name = ?, description = ? WHERE wishlist_id = ?";
        jdbcTemplate.update(sql,updatedWish.getName(),updatedWish.getDescription(),updatedWish.getId());
    }

    //Sletter et ønskeliste. (Delete funktion)
    public boolean deleteWishlist(int userId, String name){
        String sql = "DELETE FROM wishlists WHERE user_id = ? AND name = ?";
        int rowsAffected =  jdbcTemplate.update(sql,userId,name);
        return rowsAffected > 0;
    }

    public boolean deleteWish(int listId, String wishname){
        String sql = "DELETE FROM wishlist_items WHERE wishlist_id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql,listId,wishname);
        return rowsAffected > 0;
    }

    public boolean updateWishItemChecked(boolean checked, int listId, String wishname){
        String sql = "UPDATE wishlist_items SET checked = ? WHERE wishlist_id = ? AND name = ?";
        int rowsAffected = jdbcTemplate.update(sql,checked,listId,wishname);
        return rowsAffected > 0;
    }

    private RowMapper<WishlistModel> mapWishlist(){
    return (rs, rowNum) -> new WishlistModel(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
        );
    }

    private RowMapper<Item> mapWishItems(){
        return (rs, rowNum) -> new Item(
                rs.getInt("item_id"),
                rs.getString("item_name"),
                rs.getString("item_description"),
                rs.getBoolean("item_checked")
        );
    }


    private RowMapper<User> mapUsers(){
        return (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
