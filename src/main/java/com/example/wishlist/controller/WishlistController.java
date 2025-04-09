package com.example.wishlist.controller;

import com.example.wishlist.model.Item;
import com.example.wishlist.model.WishlistModel;
import com.example.wishlist.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/wishlist/login")
    public String showLogin(){
        return "login";
    }

    @PostMapping("/wishlist/login")
    public String login(@RequestParam("uid") String uid, @RequestParam("pw") String pw,
                        HttpSession session, Model model){

        if (wishlistService.login(uid,pw)){

            session.setAttribute("username", uid);
            session.setMaxInactiveInterval(500);

            return "redirect:/wishlist";
        }

        model.addAttribute("wrongCredentials",true);
        return "login";
    }

    @RequestMapping(value = "/wishlist/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/wishlist/register")
    public String showRegister(){
        return "register";
    }

    @PostMapping("/wishlist/register")
    public String register(@RequestParam("uid") String uid, @RequestParam("pw") String pw, RedirectAttributes redirectAttributes){
        if (uid.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "Username is too short. Must be at least 3 characters.");
            return "redirect:/wishlist/register";
        }

        if (pw.length() < 8) {
            redirectAttributes.addFlashAttribute("error","Password is too short. Must be at least 8 characters.");
            return "redirect:/wishlist/register";
        }

        if (!wishlistService.register(uid, pw)) {
            redirectAttributes.addFlashAttribute("error", "Account already exists.");
            return "redirect:/wishlist/register";
        }

        return "redirect:/wishlist/login";
    }



    //Viser listen af ønskelister.
    @GetMapping("/wishlist")
    public String viewWishlist(Model model, HttpSession session){
        String username = (String) session.getAttribute("username");

        if (!isLoggedIn(session)) {
            return "redirect:/wishlist/login";  // går tilbage til login hvis ikke autoriseret
        }

        // finder brugerid ud fra username
        int userId = wishlistService.getUserIdByUsername(username);

        // Finder ønskelisten ud fra brugerid
        List<WishlistModel> userWishlists = wishlistService.getWishlistsByUserId(userId);

        // sørger for at printe listen til den pågældende bruger.
        model.addAttribute("wishlist", userWishlists);
        return "wishList";
    }

    @GetMapping("/wishlist/{name}/{id}")
    public String showWishlistOfUser(@PathVariable String name, @PathVariable String id, Model model){

        List<Item> userWishlist = wishlistService.getWishItemsOfUser(Integer.parseInt(id));

        model.addAttribute("wishItemId",id);
        model.addAttribute("nameOfWishlist", name);
        model.addAttribute("wishList",userWishlist);
        return "wish-items";
    }


    //Opretter et nyt ønskeliste.
    @GetMapping("/wishlist/add")
    public String showAddWishlistForm(Model model,HttpSession session){
        if (!isLoggedIn(session)) {
            return "redirect:/wishlist/login";  // går tilbage til login hvis ikke autoriseret
        }
        WishlistModel wishlist = new WishlistModel();
        model.addAttribute("wishlist",wishlist);
        return "add-wishlist";
    }

    @GetMapping("/wishlist/{name}/{id}/add")
    public String showAddWishForm(@PathVariable String name, @PathVariable String id, Model model, HttpSession session){
        if (!isLoggedIn(session)){
            return "redirect:/wishlist/login";
        }
        Item wish = new Item();
        model.addAttribute("wish",wish);
        return "add-wish";
    }


    //Gemmer en ny ønskeliste.
    @PostMapping("/wishlist/save")
    public String saveWishlist(@ModelAttribute WishlistModel wishlist, HttpSession seesion){
        String username = (String) seesion.getAttribute("username");
        int userId = wishlistService.getUserIdByUsername(username);
        if (userId == -1) {
            return "error";
        }
        wishlistService.addWishList(wishlist,userId);
        return "redirect:/wishlist";
    }

    @PostMapping("/wishlist/{name}/{id}/save")
    public String saveWish(@ModelAttribute Item wish, @PathVariable String id){
        int listId = Integer.parseInt(id);
        wishlistService.addWish(wish,listId);
        return "redirect:/wishlist";
    }



    //Ændre en ønskeliste
    @GetMapping("/wishlist/{name}/{id}/edit")
    public String editWishList(@PathVariable String name,@PathVariable String id, Model model){
        WishlistModel wishlist = wishlistService.getWishByName(name);
        if (wishlist != null){

            wishlist.setId(Integer.parseInt(id));
            model.addAttribute("wishlist", wishlist);
            return "edit-wishlist";
        } else {
            return null;
        }
    }

    @GetMapping("/wishlist/{name}/{id}/{item}/edit")
    public String editWIsh(@PathVariable String name, @PathVariable String id, @PathVariable String item, Model model){
        Item wish = wishlistService.getSpecificWishItemOfUser(Integer.parseInt(id),item);
        if (wish != null){

            wish.setId(Integer.parseInt(id));
            model.addAttribute("wish", wish);
            return "edit-wish";
        } else {
            return null;
        }
    }

    //Gemmer ændringen - altså opdaterer en ønskeliste.
    @PostMapping("/wishlist/update")
    public String updateWishlist(@ModelAttribute WishlistModel updatedWishList) {
        wishlistService.updateWishList(updatedWishList);
        return "redirect:/wishlist";
    }

    @PostMapping("/wishlist/{name}/{id}/{item}/update")
    public String updateWish(@ModelAttribute Item updatedwish){
        wishlistService.updateWish(updatedwish);
        return "redirect:/wishlist";
    }

    //Sletter en ønskeliste.
    @PostMapping("/wishlist/{name}/{id}/delete")
    public String deleteWishlist(@PathVariable String name, HttpSession session){
        String username = (String) session.getAttribute("username");
        int userId = wishlistService.getUserIdByUsername(username);
        boolean deleted = wishlistService.deleteWishlist(userId,name);
        if (deleted) {
            return "redirect:/wishlist";
        } else {
            return null;
        }
    }

    //Sletter et ønske.
    @PostMapping("/wishlist/{listname}/{id}/{wishname}/delete")
    public String deleteWish(@PathVariable String id,@PathVariable String wishname){
        boolean deleted = wishlistService.deleteWish(Integer.parseInt(id),wishname);
        if (deleted) {
            return "redirect:/wishlist";
        } else {
            return null;
        }
    }

    @PostMapping("/wishlist/{listname}/{id}/{wishname}/toggle")
    public String toggleWishItemChecked(@PathVariable String listname, @PathVariable String id,@PathVariable String wishname, HttpSession session){
        String username = (String) session.getAttribute("username");
        Item wishItem = wishlistService.getSpecificWishItemOfUser(Integer.parseInt(id),wishname);
        if (wishItem == null){
            return "error";
        }

        wishItem.setChecked(!wishItem.isChecked());

        boolean checked = wishlistService.updateWishItemChecked(wishItem.isChecked(),username,wishname,listname);
        if (!checked){
            return "error";
        }
        return "redirect:/wishlist/" + listname;
    }
}
