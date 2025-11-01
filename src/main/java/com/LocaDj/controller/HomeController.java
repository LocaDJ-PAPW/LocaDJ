package com.LocaDj.controller;

import com.LocaDj.models.User;
import com.LocaDj.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
            if (user != null) {
                return switch (user.getRole()) {
                    case ADMIN -> "redirect:/admin/dashboard";
                    case CLIENT -> "redirect:/reservation/client/dashboard";
                };
            }
        }
        return "index";
    }
}