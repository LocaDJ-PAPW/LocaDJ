package com.LocaDj.controller;

import com.LocaDj.DTOs.ReservationFormDTO;
import com.LocaDj.models.*;
import com.LocaDj.services.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/reservations")
public class ReservaController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private KitService kitService;

    @Autowired
    private UserService userService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");


    @GetMapping("/new")
    public String showReservationForm(@RequestParam(required = false) Long kitId, Model model) {
        ReservationFormDTO form = new ReservationFormDTO();
        if (kitId != null) {
            form.setKitId(kitId);
        }
        List<Kit> kits = kitService.findAll();
        for(Kit k : kits) {
            log.info("Kit id: {} --- reservas : {}", k.getId(), k.getRents());
        }
        model.addAttribute("reservationForm", form);
        model.addAttribute("kits", kitService.findAll());
        return "reservations/form";
    }

    @PostMapping("/save")
    public String saveReservation(@AuthenticationPrincipal UserDetails userDetails,
                                  @Valid @ModelAttribute("reservationForm") ReservationFormDTO form,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("kits", kitService.findAll());
            return "reservations/form";
        }

        Optional<Kit> kitOpt = kitService.findById(form.getKitId());
        if (kitOpt.isEmpty()) {
            model.addAttribute("kits", kitService.findAll());
            model.addAttribute("availabilityError", "Kit inválido.");
            return "reservations/form";
        }

        Kit kit = kitOpt.get();
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        try {
            startDateTime = LocalDateTime.parse(form.getStartDateTime(), formatter);
            endDateTime = LocalDateTime.parse(form.getEndDateTime(), formatter);
        } catch (Exception e) {
            model.addAttribute("kits", kitService.findAll());
            model.addAttribute("availabilityError", "Formato de data/hora inválido.");
            return "reservations/form";
        }

        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();

        boolean available = reservationService.isAvailable(kit, startDateTime, endDateTime);

        if (!available) {
            model.addAttribute("kits", kitService.findAll());
            model.addAttribute("availabilityError", "Kit indisponível neste período.");
            return "reservations/form";
        }

        Reservation reservation = new Reservation();
        reservation.setKit(kit);
        reservation.setUser(user);
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);


        Reservation createdReservation = reservationService.save(reservation);
        model.addAttribute("ReservationId", createdReservation.getId());


        return "checkout/checkout";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model, @RequestParam(value = "sort", required = false) String sort) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Kit>  kits = kitService.findAll();;
        if("popular".equals(sort))
            kits = kitService.getMostPopularKits();

        List<Reservation> reservations = reservationService.findByUser(user);
        model.addAttribute("reservations", reservations);
        model.addAttribute("kits", kits);
        return "client/dashboard";
    }

    @GetMapping("/client/cancel/{id}")
    public String cancelReservation(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        Reservation reservation = reservationService.findById(id).orElseThrow(() -> new IllegalArgumentException("Reserva inválida"));
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();

        if (!reservation.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode cancelar essa reserva.");
            return "redirect:/reservations/client/dashboard";
        }

        reservationService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada com sucesso!");
        return "redirect:/reservations/client/dashboard";
    }

    @GetMapping
    public String listAllReservations(Model model) {
        List<Reservation> reservations = reservationService.findAll();
        model.addAttribute("reservations", reservations);
        return "reservations/list";
    }
}