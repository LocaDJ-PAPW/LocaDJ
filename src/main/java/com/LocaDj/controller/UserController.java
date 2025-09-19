package com.LocaDj.controller;


import com.LocaDj.models.User;
import com.LocaDj.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/registrar")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "usuarios/registrar";
    }

    @PostMapping("/registrar")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "usuarios/registrar";
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email já cadastrado");
            return "usuario/registrar";
        }
        user.setRole(User.Role.CLIENT);
        userService.saveUser(user);
        return "redirect:/login?registrado";
    }


    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.listAll());
        return "usuarios/lista";
    }
}