package com.LocaDj.repositories;

import com.LocaDj.models.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KitRepository extends JpaRepository<Kit, Long> {


    int countByQuantityGreaterThan(int i);

    @Query("SELECT r.kit.name, COUNT(r) FROM Reservation r GROUP BY r.kit.name ORDER BY COUNT(r) DESC")
    List<Object[]> countReservationsPerKit();
}
