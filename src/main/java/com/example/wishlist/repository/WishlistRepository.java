package com.example.wishlist.repository;

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

    //Tilføjer et ønske til databasen. (Create funktion)
    public void addWish(WishlistModel wish){
        try {

            String sql = "INSERT INTO wishlist (name, description) VALUES (?,?)";
            jdbcTemplate.update(sql,wish.getName(),wish.getDescription());

            /*String idSql = "SELECT id FROM wishlist WHERE name = ?";
            Integer wishId = jdbcTemplate.queryForObject(idSql, Integer.class,wish.getName());
            */
        } catch (DuplicateKeyException ignored){
        }
    }

    //Lister alle ønsker. (Read funktion)
    public List<WishlistModel> getAllWishes(){
        String sql = "SELECT * FROM wishlist";
        return jdbcTemplate.query(sql, mapWishes());
    }

    //Henter et ønske ud fra navnet. (Read funktion)
    public WishlistModel getWishByName(String name){
        try {
            String sql = "SELECT * FROM wishlist WHERE name = ?";
            return jdbcTemplate.queryForObject(sql,mapWishes(),name);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    //Opdaterer et ønske, hvis der opstår en ændring. (Update funktion)
    public void updateWish(WishlistModel updatedWish){
        String sql = "UPDATE wishlist SET description = ? WHERE name = ?";
        jdbcTemplate.update(sql,updatedWish.getDescription(),updatedWish.getName());
    }

    //Sletter et ønske. (Delete funktion)
    public boolean deleteWish(String name){
        String sql = "DELETE FROM wishlist WHERE name = ?";
        int rowsAffected =  jdbcTemplate.update(sql,name);
        return rowsAffected > 0;
    }

    private RowMapper<WishlistModel> mapWishes(){
    return (rs, rowNum) -> new WishlistModel(
            rs.getString("name"),
            rs.getString("description")
        );
    }
}
