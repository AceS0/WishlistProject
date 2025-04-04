package com.example.wishlist.controller;

import com.example.wishlist.model.Item;
import com.example.wishlist.model.WishlistModel;
import com.example.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("")
public class WishlistController {
    private final WishlistService wishlistService;
    public WishlistController(WishlistService wishlistService){
        this.wishlistService = wishlistService;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    @GetMapping("/wishes/login")
    public String showLogin(){
        return "login";
    }

    @PostMapping("/wishes/login")
    public String login(@RequestParam("uid") String uid, @RequestParam("pw") String pw,
                        HttpSession session, Model model){

        if (wishlistService.login(uid,pw)){

            session.setAttribute("username", uid);
            session.setMaxInactiveInterval(500);

            return "redirect:/wishes";
        }

        model.addAttribute("wrongCredentials",true);
        return "login";
    }

    @GetMapping("/wishes/register")
    public String showRegister(){
        return "register";
    }

    @PostMapping("/wishes/register")
    public String register(@RequestParam("uid") String uid, @RequestParam("pw") String pw, RedirectAttributes redirectAttributes){
        if (uid.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "Username is too short. Must be at least 3 characters.");
            return "redirect:/wishes/register";
        }

        if (!wishlistService.register(uid, pw)) {
            redirectAttributes.addFlashAttribute("error", "Account already exists.");
            return "redirect:/wishes/register";
        }

        return "redirect:/wishes/login";
    }



    //Viser listen af ønsker.
    @GetMapping("/wishes")
    public String viewWishes(Model model, HttpSession session){
        String username = (String) session.getAttribute("username");

        if (!isLoggedIn(session)) {
            return "redirect:/wishes/login";  // går tilbage til login hvis ikke autoriseret
        }

        // finder brugerid ud fra username
        int userId = wishlistService.getUserIdByUsername(username);

        // Finder ønskelisten ud fra brugerid
        List<WishlistModel> userWishlists = wishlistService.getWishlistsByUserId(userId);

        // sørger for at printe listen til den pågældende bruger.
        model.addAttribute("wishes", userWishlists);
        return "wishList";
    }

    @GetMapping("/wishes/{name}")
    public String showWishlistOfUser(@PathVariable String name,HttpSession session, Model model){
        String username = (String) session.getAttribute("username");
        List<Item> userWishes = wishlistService.getWishItemsOfUser(username,name);
        model.addAttribute("nameOfWishlist", name);
        model.addAttribute("wishList",userWishes);
        return "wish-items";
    }

    //Dykker ned i et ønskelistes item.
    @GetMapping("/wishes/{name}/{item}")
    public String showWishlistItemOfUser(@PathVariable String name, @PathVariable String item, HttpSession session, Model model){
        String username = (String) session.getAttribute("username");
        Item wishItem = wishlistService.getSpecificWishItemOfUser(username,name,item);
        if (wishItem == null){
            return "error";
        }

        model.addAttribute("wishItem", wishItem);
        return "wishlist-details";
    }


    //Opretter et nyt ønskeliste.
    @GetMapping("/wishes/add")
    public String showAddWishForm(Model model){
        WishlistModel wish = new WishlistModel();
        model.addAttribute("wish",wish);
        return "add-wish";
    }



    //Gemmer et nyt ønske.
    @PostMapping("/wishes/save")
    public String saveWish(@ModelAttribute WishlistModel wish, Principal principal){
        String username = principal.getName();
        int userId = wishlistService.getUserIdByUsername(username);
        if (userId == -1) {
            return "error";
        }
        wishlistService.addWish(wish,userId);
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
