package com.LocaDj.services;

import org.springframework.stereotype.Service;
import com.LocaDj.models.*;
import com.LocaDj.repositories.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public boolean isAvailable(Kit kit, LocalDateTime start, LocalDateTime end) {
        List<Reservation> overlapping = reservationRepository
                .findByKitAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(kit, end, start);
        return overlapping.size() < kit.getQuantity();
    }

    public Reservation save(@Valid Reservation reservation) {
        Kit kit = reservation.getKit();
        kit.setQuantity(kit.getQuantity() - 1);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findByUser(User user) {
        return reservationRepository.findAll()
                .stream()
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .toList();
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public void deleteById(Long id) {
        Reservation reservation = reservationRepository.getReferenceById(id);
        Kit kit = reservation.getKit();
        kit.setQuantity(kit.getQuantity() + 1);
        reservationRepository.deleteById(id);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }
}
