package com.health.check.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check-application")
public class ApplicationCheckController {
    @GetMapping
    public ResponseEntity<String> checkHealthApplication() {

        return ResponseEntity.ok("Health Status OK");
    }
}
