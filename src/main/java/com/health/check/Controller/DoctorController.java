package com.health.check.Controller;

import com.health.check.Dto.DoctorDto;
import com.health.check.Dto.DoctorProfileResponse;
import com.health.check.Service.DoctorService;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Doctor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Operation(summary = "Get Doctors")
    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false) Double maxConsultationFee) {
        List<Doctor> doctors = doctorService.getDoctors(specialization, experienceYears, maxConsultationFee);
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Get Logged In Doctor Profile")
    @GetMapping("/doctors/profile")
    public ResponseEntity<?> getDoctorProfile() throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(doctorService.getDoctorByEmail(email));
    }

    @Operation(summary = "Get Doctor By Id")
    @GetMapping("/doctors/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) throws NotFoundException {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @Operation(summary = "Update Logged In Doctor Details")
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/doctors")
    public ResponseEntity<?> updateDoctor(@RequestBody DoctorDto req) throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(doctorService.updateDoctor(email, req));
    }

    @Operation(summary = "Delete Logged In Doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/doctors")
    public ResponseEntity<?> deleteDoctor() throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        DoctorProfileResponse doctorProfileResponse = doctorService.getDoctorByEmail(email);
        Doctor doctor = doctorProfileResponse.getDoctor();
        doctorService.deleteDoctor(doctor.getId());
        return ResponseEntity.ok("Doctor deleted successfully");
    }
}



