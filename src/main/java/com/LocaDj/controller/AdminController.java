package com.LocaDj.controller;

import com.LocaDj.repositories.ReservationRepository;
import com.LocaDj.services.KitService;
import com.LocaDj.services.ReservationService;
import com.LocaDj.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    private final KitService kitService;

    private final ReservationService reservationService;

    public AdminController(KitService kitService, ReservationService reservationService) {
        this.kitService = kitService;
        this.reservationService = reservationService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.userCount());
        model.addAttribute("availableKitsCount", kitService.kitsAvailable());
        model.addAttribute("activeReservationsCount", reservationService.activeReservations());
        model.addAttribute("reservationsData", reservationService.getReservationsPerMonthData());
        model.addAttribute("topKitsData", kitService.getTopKitsData());

        return "admin/dashboard";
    }
}