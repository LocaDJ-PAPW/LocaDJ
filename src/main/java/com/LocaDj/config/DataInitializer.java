package com.LocaDj.config;

import com.LocaDj.models.Kit;
import com.LocaDj.models.User;
import com.LocaDj.repositories.KitRepository;
import com.LocaDj.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KitRepository kitRepository;

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
}