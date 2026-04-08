package br.tec.llam.biblioteca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "library-api",
                "version", "0.0.1-SNAPSHOT"
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "name", "LLAM Biblioteca API",
                "description", "API REST para gerenciamento de biblioteca digital multi-tenant",
                "version", "0.0.1-SNAPSHOT",
                "java", System.getProperty("java.version"),
                "spring.profiles.active", System.getProperty("spring.profiles.active", "default")
        ));
    }
}
