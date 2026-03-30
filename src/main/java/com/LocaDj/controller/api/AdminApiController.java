package com.LocaDj.controller.api;

import com.LocaDj.services.KitService;
import com.LocaDj.services.ReservationService;
import com.LocaDj.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserService userService;
    private final KitService kitService;
    private final ReservationService reservationService;

    public AdminApiController(UserService userService, KitService kitService, ReservationService reservationService) {
        this.userService = userService;
        this.kitService = kitService;
        this.reservationService = reservationService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.userCount());
        data.put("availableKitsCount", kitService.kitsAvailable());
        data.put("activeReservationsCount", reservationService.activeReservations());
        data.put("reservationsData", reservationService.getReservationsPerMonthData());
        data.put("topKitsData", kitService.getTopKitsData());
        return data;
    }
}