package com.health.check.controller;

import com.health.check.dto.DoctorDto;
import com.health.check.dto.DoctorProfileResponse;
import com.health.check.service.DoctorService;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Doctor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Get Doctors")
    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false) Double maxConsultationFee) {
        List<Doctor> doctors = doctorService.getDoctors(specialization, experienceYears, maxConsultationFee);
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Get Logged In Doctor Profile")
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctors/profile")
    public ResponseEntity<DoctorProfileResponse> getDoctorProfile() throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(doctorService.getDoctorByEmail(email));
    }

    @Operation(summary = "Get Doctor By Id")
    @GetMapping("/doctors/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) throws NotFoundException {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @Operation(summary = "Update Logged In Doctor Details")
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/doctors")
    public ResponseEntity<Doctor> updateDoctor(@RequestBody DoctorDto req) throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(doctorService.updateDoctor(email, req));
    }

    @Operation(summary = "Delete Logged In Doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/doctors")
    public ResponseEntity<String> deleteDoctor() throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        DoctorProfileResponse doctorProfileResponse = doctorService.getDoctorByEmail(email);
        Doctor doctor = doctorProfileResponse.getDoctor();
        doctorService.deleteDoctor(doctor.getId());
        return ResponseEntity.ok("Doctor deleted successfully");
    }
}



