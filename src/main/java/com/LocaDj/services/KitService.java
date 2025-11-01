package com.LocaDj.services;

import com.LocaDj.models.Kit;
import com.LocaDj.repositories.KitRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KitService {

    @Autowired
    private KitRepository kitRepository;

    public List<Kit> findAll() {
        return kitRepository.findAll();
    }

    public Optional<Kit> findById(Long id) {
        return kitRepository.findById(id);
    }

    public Kit save(@Valid Kit kit) {
        return kitRepository.save(kit);
    }

    public void deleteById(Long id) {
        kitRepository.deleteById(id);
    }

    public int kitsAvailable(){
        return kitRepository.countByQuantityGreaterThan(0);
    }

    public Map<String, Object> getTopKitsData() {
        // 1. Executa a consulta de agregação
        List<Object[]> results = kitRepository.countReservationsPerKit();

        List<String> kits = new ArrayList<>();
        List<Long> reservations = new ArrayList<>();

        // 2. Mapeia os resultados (Object[] {Nome do Kit, Contagem})
        for (Object[] result : results) {
            String kitName = (String) result[0];
            Long count = (Long) result[1];

            kits.add(kitName);
            reservations.add(count);
        }

        // 3. Formata o Map para o JavaScript
        Map<String, Object> data = new HashMap<>();
        data.put("kits", kits);
        data.put("reservations", reservations);

        return data;
    }
}