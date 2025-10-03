package com.LocaDj.controller;

import com.LocaDj.models.Kit;
import com.LocaDj.services.KitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/kits")
public class KitController {

    @Autowired
    private KitService kitService;

    @GetMapping
    public String listKits(Model model) {
        model.addAttribute("kits", kitService.findAll());
        return "kits/list";
    }

    @GetMapping("/new")
    public String showNewKitForm(Model model) {
        model.addAttribute("kit", new Kit());
        return "kits/form";
    }

    @PostMapping("/save")
    public String saveKit(@Valid @ModelAttribute("kit") Kit kit, BindingResult result) {
        if (result.hasErrors()) {
            return "kits/form";
        }
        kitService.save(kit);
        return "redirect:/kits";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Kit kit = kitService.findById(id).orElseThrow(() -> new IllegalArgumentException("Kit inválido: " + id));
        model.addAttribute("kit", kit);
        return "kits/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteKit(@PathVariable("id") Long id) {
        kitService.deleteById(id);
        return "redirect:/kits";
    }
}