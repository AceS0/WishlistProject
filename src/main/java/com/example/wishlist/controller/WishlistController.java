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

    // Dependency injection af WishlistService
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService){
        this.wishlistService = wishlistService;
    }

    // Tjekker om brugeren er logget ind via sessionen
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    // Viser forsiden
    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    // Viser login-siden
    @GetMapping("/wishlist/login")
    public String showLogin(){
        return "login";
    }

    // Håndterer login-logik og gemmer brugernavn i session
    @PostMapping("/wishlist/login")
    public String login(@RequestParam("uid") String uid, @RequestParam("pw") String pw,
                        HttpSession session, Model model){

        if (wishlistService.login(uid,pw)){
            session.setAttribute("username", uid);
            session.setMaxInactiveInterval(500);  // Sessionen udløber efter 500 sekunder
            return "redirect:/wishlist";
        }

        model.addAttribute("wrongCredentials",true);
        return "login";
    }

    // Logger brugeren ud ved at invalidere sessionen
    @RequestMapping(value = "/wishlist/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    // Viser registreringssiden
    @GetMapping("/wishlist/register")
    public String showRegister(){
        return "register";
    }

    // Håndterer registrering og validerer input
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

    // Viser brugerens ønskelister
    @GetMapping("/wishlist")
    public String viewWishlist(Model model, HttpSession session){
        String username = (String) session.getAttribute("username");

        if (!isLoggedIn(session)) {
            return "redirect:/wishlist/login";  // Sender til login, hvis ikke logget ind
        }

        int userId = wishlistService.getUserIdByUsername(username);
        List<WishlistModel> userWishlists = wishlistService.getWishlistsByUserId(userId);

        model.addAttribute("wishlist", userWishlists);
        return "wishList";
    }

    // Viser ønskerne på en specifik ønskeliste
    @GetMapping("/wishlist/{name}/{id}")
    public String showWishlistOfUser(@PathVariable String name, @PathVariable String id, Model model){
        List<Item> userWishlist = wishlistService.getWishItemsOfUser(Integer.parseInt(id));

        model.addAttribute("wishListItemId",id);
        model.addAttribute("nameOfWishlist", name);
        model.addAttribute("wishList",userWishlist);
        return "wish-items";
    }

    // Viser formularen til at oprette en ny ønskeliste
    @GetMapping("/wishlist/add")
    public String showAddWishlistForm(Model model,HttpSession session){
        if (!isLoggedIn(session)) {
            return "redirect:/wishlist/login";
        }
        WishlistModel wishlist = new WishlistModel();
        model.addAttribute("wishlist",wishlist);
        return "add-wishlist";
    }

    // Viser formularen til at tilføje et ønske til en ønskeliste
    @GetMapping("/wishlist/{name}/{id}/add")
    public String showAddWishForm(@PathVariable String name, @PathVariable String id, Model model, HttpSession session){
        if (!isLoggedIn(session)){
            return "redirect:/wishlist/login";
        }
        Item wish = new Item();
        model.addAttribute("wish",wish);
        return "add-wish";
    }

    // Gemmer en ny ønskeliste
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

    // Gemmer et nyt ønske
    @PostMapping("/wishlist/{name}/{id}/save")
    public String saveWish(@ModelAttribute Item wish, @PathVariable String id){
        int listId = Integer.parseInt(id);
        wishlistService.addWish(wish,listId);
        return "redirect:/wishlist";
    }

    // Viser redigeringsformular for en ønskeliste
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

    // Viser redigeringsformular for et ønske
    @GetMapping("/wishlist/{name}/{id}/{item}/{wishId}/edit")
    public String editWIsh(@PathVariable String name, @PathVariable String id, @PathVariable String item, Model model, @PathVariable String wishId){
        Item wish = wishlistService.getSpecificWishItemOfUser(Integer.parseInt(id),item, Integer.parseInt(wishId));
        if (wish != null){
            wish.setId(Integer.parseInt(id));
            model.addAttribute("wish", wish);
            model.addAttribute("name", name);
            model.addAttribute("item", item);
            model.addAttribute("id", id);
            model.addAttribute("wishId", wishId);
            return "edit-wish";
        } else {
            return null;
        }
    }

    // Gemmer ændringer til en ønskeliste
    @PostMapping("/wishlist/update")
    public String updateWishlist(@ModelAttribute WishlistModel updatedWishList) {
        wishlistService.updateWishList(updatedWishList);
        return "redirect:/wishlist";
    }

    // Gemmer ændringer til et ønske
    @PostMapping("/wishlist/{name}/{id}/{item}/{wishId}/update")
    public String updateWish(@ModelAttribute Item updatedwish, @PathVariable String item, @PathVariable String wishId){
        wishlistService.updateWish(updatedwish,Integer.parseInt(wishId));
        return "redirect:/wishlist";
    }

    // Sletter en ønskeliste
    @PostMapping("/wishlist/{name}/{id}/delete")
    public String deleteWishlist(@PathVariable String name,@PathVariable String id){
        boolean deleted = wishlistService.deleteWishlist(Integer.parseInt(id),name);
        if (deleted) {
            return "redirect:/wishlist";
        } else {
            return null;
        }
    }

    // Sletter et ønske
    @PostMapping("/wishlist/{listname}/{id}/{wishname}/{wishId}/delete")
    public String deleteWish(@PathVariable String id, @PathVariable String wishname, @PathVariable String listname){
        boolean deleted = wishlistService.deleteWish(Integer.parseInt(id),wishname);
        if (deleted) {
            return "redirect:/wishlist";
        } else {
            return null;
        }
    }

    // Toggler (ændrer) tjekket-status på et ønske
    @PostMapping("/wishlist/{listname}/{id}/{wishname}/{wishId}/toggle")
    public String toggleWishItemChecked(@PathVariable String listname, @PathVariable String id, @PathVariable String wishname, @PathVariable String wishId){
        Item wishItem = wishlistService.getSpecificWishItemOfUser(Integer.parseInt(id),wishname,Integer.parseInt(wishId));
        if (wishItem == null){
            return "error";
        }
        wishItem.setChecked(!wishItem.isChecked());  // Skifter mellem true/false
        boolean checked = wishlistService.updateWishItemChecked(wishItem.isChecked(),Integer.parseInt(id),wishname,Integer.parseInt(wishId));
        if (!checked){
            return "error";
        }
        return "redirect:/wishlist/" + listname;
    }
}
