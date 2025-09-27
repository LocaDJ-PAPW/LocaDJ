package com.LocaDj.services;

import com.LocaDj.models.Kit;
import com.LocaDj.repositories.KitRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}