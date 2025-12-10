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

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false) Double consultationFee) {

        List<Doctor> doctors = doctorService.getDoctors(specialization, experienceYears, consultationFee);
        return ResponseEntity.ok(doctors);
    }

    // ✅ Update Doctor
    @PutMapping("/doctors/{id}")
    public ResponseEntity<String> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok("Doctor updated successfully");
    }

    // ✅ Delete Doctor
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok("Doctor deleted successfully");
    }
    }



