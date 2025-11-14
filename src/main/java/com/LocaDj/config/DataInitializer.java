package com.LocaDj.config;

import com.LocaDj.models.Kit;
import com.LocaDj.models.Reservation;
import com.LocaDj.models.Status;
import com.LocaDj.models.User;
import com.LocaDj.repositories.KitRepository;
import com.LocaDj.repositories.ReservationRepository;
import com.LocaDj.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KitRepository kitRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@locadj.com").isEmpty()) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@locadj.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
        }

        if (kitRepository.count() == 0) {
            Kit[] kits = {
                    createKit("Kit Básico DJ", "Equipamento básico para DJ", 100.0, 5, "https://st4.depositphotos.com/10614052/24724/i/450/depositphotos_247249704-stock-photo-modern-mixer-dark-background.jpg"),
                    createKit("Kit Avançado DJ", "Equipamento avançado para DJs experientes", 200.0, 3, "https://img.freepik.com/fotos-gratis/vista-da-cabine-de-dj-futurista_23-2151072972.jpg?semt=ais_items_boosted&w=740"),
                    createKit("Kit Iluminação", "Iluminação para eventos", 150.0, 4, "https://as2.ftcdn.net/jpg/02/37/33/79/1000_F_237337928_SPzbTCblRLDez8PG3e9fzykrU1wvmqv4.jpg"),
                    createKit("Kit Som", "Sistema de som completo", 300.0, 2, "https://static.vecteezy.com/ti/fotos-gratis/t1/36393141-ai-gerado-eletronico-danca-musica-edm-fundo-eletro-som-poster-techno-danca-bandeira-abstrato-dj-musica-cobrir-gratis-foto.jpeg"),
                    createKit("Kit Microfones", "Microfones para eventos", 80.0, 10, "https://media.istockphoto.com/id/629961180/photo/on-the-air.jpg?s=612x612&w=0&k=20&c=BiFLJF6-WdOYIKin2BBqvR39d_6N5JdZuZYBlrfG6vg="),
                    createKit("Kit Acessórios", "Acessórios diversos para DJ", 50.0, 15, "https://png.pngtree.com/thumb_back/fh260/background/20231007/pngtree-d-rendering-of-multicolored-background-with-dj-mixing-turntable-headphones-and-image_13599897.png")
            };
            for (Kit kit : kits) {
                kitRepository.save(kit);
            }

        }
        if(reservationRepository.count() < 10){
            List<Reservation> reservations = generatePastReservations();
            for(Reservation reservation : reservations){
                reservationRepository.save(reservation);
            }
        }


    }

    private Kit createKit(String name, String desc, double price, int qty, String imageUrl) {
        Kit kit = new Kit();
        kit.setName(name);
        kit.setDescription(desc);
        kit.setPricePerDay(price);
        kit.setQuantity(qty);
        kit.setImageUrl(imageUrl);
        return kit;
    }

    private List<Reservation> generatePastReservations() {
        List<Reservation> reservations = new ArrayList<>();
        List<Kit> kits = kitRepository.findAll();

        reservations.add(createReservation(2, kits.get(0),
                LocalDateTime.of(2025, 9, 5, 10, 0),
                LocalDateTime.of(2025, 9, 10, 10, 0),
                5, kits.get(0).getPricePerDay() * 5, Status.CONCLUIDA));

        reservations.add(createReservation(3, kits.get(1),
                LocalDateTime.of(2025, 9, 15, 9, 0),
                LocalDateTime.of(2025, 9, 20, 9, 0),
                5, kits.get(1).getPricePerDay() * 5, Status.CONCLUIDA));

        reservations.add(createReservation(4, kits.get(2),
                LocalDateTime.of(2025, 9, 2, 14, 0),
                LocalDateTime.of(2025, 9, 6, 14, 0),
                4, kits.get(2).getPricePerDay() * 4, Status.CONCLUIDA));

        reservations.add(createReservation(5, kits.get(0),
                LocalDateTime.of(2025, 9, 10, 8, 0),
                LocalDateTime.of(2025, 9, 12, 8, 0),
                2, kits.get(0).getPricePerDay() * 2, Status.CONCLUIDA));

        reservations.add(createReservation(6, kits.get(3),
                LocalDateTime.of(2025, 9, 1, 9, 0),
                LocalDateTime.of(2025, 9, 5, 9, 0),
                4, kits.get(3).getPricePerDay() * 4, Status.CONCLUIDA));

        reservations.add(createReservation(7, kits.get(2),
                LocalDateTime.of(2025, 4, 15, 13, 0),
                LocalDateTime.of(2025, 4, 18, 13, 0),
                3, kits.get(2).getPricePerDay() * 3, Status.CONCLUIDA));

        reservations.add(createReservation(8, kits.get(1),
                LocalDateTime.of(2025, 3, 21, 9, 0),
                LocalDateTime.of(2025, 3, 23, 9, 0),
                2, kits.get(1).getPricePerDay() * 2, Status.CONCLUIDA));

        reservations.add(createReservation(9, kits.get(0),
                LocalDateTime.of(2025, 2, 10, 10, 0),
                LocalDateTime.of(2025, 2, 15, 10, 0),
                5, kits.get(0).getPricePerDay() * 5, Status.CONCLUIDA));

        reservations.add(createReservation(10, kits.get(4),
                LocalDateTime.of(2025, 1, 25, 11, 0),
                LocalDateTime.of(2025, 1, 30, 11, 0),
                5, kits.get(4).getPricePerDay() * 5, Status.CONCLUIDA));

        reservations.add(createReservation(2, kits.get(3),
                LocalDateTime.of(2024, 12, 10, 9, 0),
                LocalDateTime.of(2024, 12, 12, 9, 0),
                2, kits.get(3).getPricePerDay() * 2, Status.CONCLUIDA));

        return reservations;
    }

    private Reservation createReservation(long userId, Kit kit, LocalDateTime start, LocalDateTime end, int daily, double total, Status status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + userId));;
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setKit(kit);
        reservation.setStartDateTime(start);
        reservation.setEndDateTime(end);
        reservation.setDaily(daily);
        reservation.setTotalAmount(total);
        reservation.setStatus(status);
        return reservation;
    }


}