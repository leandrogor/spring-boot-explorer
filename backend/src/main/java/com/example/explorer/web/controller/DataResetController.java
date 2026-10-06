package com.example.explorer.web.controller;

import com.example.explorer.repository.inmemory.SeedDataInitializer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataResetController {

    private final SeedDataInitializer seedDataInitializer;

    public DataResetController(SeedDataInitializer seedDataInitializer) {
        this.seedDataInitializer = seedDataInitializer;
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetData() {
        seedDataInitializer.resetData();
        return ResponseEntity.ok(Map.of(
                "message", "Datos reiniciados",
                "timestamp", Instant.now()
        ));
    }
}
