package com.LocaDj.repositories;

import com.LocaDj.models.Kit;
import com.LocaDj.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByKitAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
            Kit kit, LocalDateTime endDateTime, LocalDateTime startDateTime);
}
