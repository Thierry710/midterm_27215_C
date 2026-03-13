package auca.ac.rw.onlineVotingSystem.controller;

import auca.ac.rw.onlineVotingSystem.model.*;
import auca.ac.rw.onlineVotingSystem.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private final UserService userService;
    private final LocationService locationService;

    public MainController(UserService userService, LocationService locationService) {
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("villages", locationService.getAllVillages());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role,
                           @RequestParam(required = false) Long villageId) {
        if (userService.usernameExists(username)) {
            return "redirect:/register?error=userExists";
        }
        User user = userService.registerUser(username, password, role);
        if (villageId != null) {
            Village village = locationService.findVillageById(villageId);
            user.setVillage(village);
            userService.save(user);
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/default")
    public String redirectAfterLogin(Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        if (user.getRole() == auca.ac.rw.onlineVotingSystem.enums.UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole() == auca.ac.rw.onlineVotingSystem.enums.UserRole.CANDIDATE) {
            return "redirect:/candidate/dashboard";
        } else {
            return "redirect:/voter/dashboard";
        }
    }
}
