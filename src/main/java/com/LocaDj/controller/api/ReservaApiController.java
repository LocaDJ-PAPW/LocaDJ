package com.LocaDj.controller.api;

import com.LocaDj.DTOs.ReservationFormDTO;
import com.LocaDj.models.*;
import com.LocaDj.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservaApiController {

    @Autowired private ReservationService reservationService;
    @Autowired private KitService kitService;
    @Autowired private UserService userService;

    @GetMapping
    public List<Reservation> getMyReservations(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        return reservationService.findByUser(user);
    }

    @GetMapping("/{id}")
    public Optional<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody ReservationFormDTO form) {

        Kit kit = kitService.findById(form.getKitId()).orElse(null);
        if (kit == null) return ResponseEntity.badRequest().body("Kit não encontrado");

        LocalDateTime start = LocalDateTime.parse(form.getStartDateTime());
        LocalDateTime end = LocalDateTime.parse(form.getEndDateTime());

        if (!reservationService.isAvailable(kit, start, end)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Kit indisponível no período");
        }

        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();

        Reservation res = new Reservation();
        res.setKit(kit);
        res.setUser(user);
        res.setStartDateTime(start);
        res.setEndDateTime(end);

        Reservation saved = reservationService.save(res);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}/left-for-delivery")
    public ResponseEntity<Void> leftForDelivery(@PathVariable("id") long id) {
        reservationService.LeftForDelivery(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/in-progress")
    public ResponseEntity<Void> inProgress(@PathVariable("id") long id) {
        reservationService.InProgress(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/completed")
    public ResponseEntity<Void> completed(@PathVariable("id") long id) {
        reservationService.completed(id);
        return ResponseEntity.noContent().build();
    }
}