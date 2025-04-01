package com.example.wishlist.repository;

import com.example.wishlist.model.User;
import com.example.wishlist.model.WishlistModel;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WishlistRepository {
    private final JdbcTemplate jdbcTemplate;

    public WishlistRepository(JdbcTemplate jdbcTemplate){
    this.jdbcTemplate = jdbcTemplate;
    }

    public User getUser(String uid){
        String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql,mapUsers(),uid);
    }

    //Tilføjer et ønske til databasen. (Create funktion)
    public void addWish(WishlistModel wish, int userId){
        try {

            String sql = "INSERT INTO wishlists (user_id,name, description) VALUES (?,?,?)";
            jdbcTemplate.update(sql,userId,wish.getName(),wish.getDescription());

            /*String idSql = "SELECT id FROM wishlist WHERE name = ?";
            Integer wishId = jdbcTemplate.queryForObject(idSql, Integer.class,wish.getName());
            */
        } catch (DuplicateKeyException ignored){
        }
    }


    //Lister alle ønsker. (Read funktion)
    public List<WishlistModel> getAllWishes(){
        String sql = "SELECT * FROM wishlists";
        return jdbcTemplate.query(sql, mapWishes());
    }

    //Henter et ønske ud fra navnet. (Read funktion)
    public WishlistModel getWishByName(String name){
        try {
            String sql = "SELECT * FROM wishlists WHERE name = ?";
            return jdbcTemplate.queryForObject(sql,mapWishes(),name);
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
        return jdbcTemplate.query(sql, mapWishes(), userId);
    }

    //Opdaterer et ønske, hvis der opstår en ændring. (Update funktion)
    public void updateWish(WishlistModel updatedWish){
        String sql = "UPDATE wishlists SET description = ? WHERE name = ?";
        jdbcTemplate.update(sql,updatedWish.getDescription(),updatedWish.getName());
    }

    //Sletter et ønske. (Delete funktion)
    public boolean deleteWish(String name){
        String sql = "DELETE FROM wishlists WHERE name = ?";
        int rowsAffected =  jdbcTemplate.update(sql,name);
        return rowsAffected > 0;
    }

    private RowMapper<WishlistModel> mapWishes(){
    return (rs, rowNum) -> new WishlistModel(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description")
        );
    }

    private RowMapper<User> mapUsers(){
        return (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
