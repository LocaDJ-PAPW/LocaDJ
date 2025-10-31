package com.LocaDj.services;

import org.springframework.stereotype.Service;
import com.LocaDj.models.*;
import com.LocaDj.repositories.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


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
        reservation.setDaily(Days(reservation.getStartDateTime(), reservation.getEndDateTime()));
        reservation.setTotalAmount(reservation.getDaily() * kit.getPricePerDay());
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

    public int Days(LocalDateTime initialDate, LocalDateTime finalDate) {
        long minutes = ChronoUnit.MINUTES.between(initialDate, finalDate);
        if (minutes <= 0)
            return 0;

        double minutesPerDay = 1440;

        double days = (double) minutes / minutesPerDay;
        return (int) Math.ceil(days);


    }

    public int activeReservations(){
        return reservationRepository.countByStatus(Status.CONFIRMADA);

    }

    public Map<String, Object> getReservationsPerMonthData() {

        List<Object[]> results = reservationRepository.countReservationsPerMonth();

        List<String> months = new ArrayList<>();
        List<Long> reservations = new ArrayList<>();


        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            Integer month = (Integer) result[1];
            Long count = (Long) result[2];


            String monthName = java.time.Month.of(month).name();
            months.add(monthName + " " + year);
            reservations.add(count);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("months", months);
        data.put("reservations", reservations);

        return data;
    }
}
