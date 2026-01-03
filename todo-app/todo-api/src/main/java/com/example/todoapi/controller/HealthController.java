package com.example.todoapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    private final MongoTemplate mongoTemplate;

    public HealthController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "database", "connected"
            ));
        } catch (Exception e) {
            log.warn("Health check failed: MongoDB connection error", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "status", "error",
                            "database", "disconnected"
                    ));
        }
    }
}
