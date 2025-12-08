package com.health.check.Controller;

import com.health.check.Service.DoctorService;
import com.health.check.models.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/admin/doctor/create")
    private ResponseEntity<String> createDoctor(@RequestBody Doctor doctor) {
        doctorService.createDoctor(doctor);
        return ResponseEntity.ok("Doctor created successfully");
    }

    @GetMapping()