package com.LocaDj.controller.api;

import com.LocaDj.models.Kit;
import com.LocaDj.services.KitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
public class KitApiController {

    @Autowired
    private KitService kitService;

    @GetMapping
    public List<Kit> getAll() {
        return kitService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kit> getById(@PathVariable Long id) {
        return kitService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Kit create(@RequestBody Kit kit) {
        return kitService.save(kit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}