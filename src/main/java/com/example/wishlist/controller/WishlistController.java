package com.example.wishlist.controller;

import com.example.wishlist.model.WishlistModel;
import com.example.wishlist.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
public class WishlistController {
    private final WishlistService wishlistService;
    public WishlistController(WishlistService wishlistService){
        this.wishlistService = wishlistService;
    }

    //Viser listen af ønsker.
    @GetMapping("/wishes")
    public String viewWishes(Model model){
        model.addAttribute("wishes",wishlistService.getAllWishes());
        return "wishList";
    }

    //Dykker ned i et ønske.
    @GetMapping("/wishes/{name}")
    public ResponseEntity<WishlistModel> getWishByName(@PathVariable String name){
        WishlistModel wish = wishlistService.getWishByName(name);
        return (wish != null) ? ResponseEntity.ok(wish) : ResponseEntity.notFound().build();
    }

    //Opretter et nyt ønske.
    @GetMapping("/wishes/add")
    public String showAddWishForm(Model model){
        WishlistModel wish = new WishlistModel();
        model.addAttribute("wish",wish);
        return "add-wish";
    }

    //Gemmer et nyt ønske.
    @PostMapping("/wishes/save")
    public String saveWish(@ModelAttribute WishlistModel wish){
        wishlistService.addWish(wish);
        return "redirect:/wishes";
    }

    //Ændre et ønske
    @GetMapping("/wishes/{name}/edit")
    public String editWish(@PathVariable String name, Model model){
        WishlistModel wish = wishlistService.getWishByName(name);
        if (wish != null){

            model.addAttribute("wish", wish);
            return "edit-wish";
        } else {
            return null;
        }
    }

    //Gemmer ændringen - altså opdaterer et ønske.
    @PostMapping("/wishes/update")
    public String updateWish(@ModelAttribute WishlistModel updatedWish) {
        wishlistService.updateWish(updatedWish);
        return "redirect:/wishes";
    }

    //Sletter et ønske.
    @PostMapping("/wishes/{name}/delete")
    public String deleteWish(@PathVariable String name){
        boolean deleted = wishlistService.deleteWish(name);
        if (deleted) {
            return "redirect:/wishes";
        } else {
            return null;
        }
    }
}
