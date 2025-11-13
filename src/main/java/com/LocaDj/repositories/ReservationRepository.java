package com.LocaDj.repositories;

import com.LocaDj.models.Kit;
import com.LocaDj.models.Reservation;
import com.LocaDj.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByKitAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
            Kit kit, LocalDateTime endDateTime, LocalDateTime startDateTime);

    int countByStatus(Status status);

    @Query("SELECT YEAR(r.startDateTime), MONTH(r.startDateTime), COUNT(r) " +
            "FROM Reservation r " +
            "GROUP BY YEAR(r.startDateTime), MONTH(r.startDateTime) " +
            "ORDER BY YEAR(r.startDateTime) DESC, MONTH(r.startDateTime) DESC")
    List<Object[]> countReservationsPerMonth();
}