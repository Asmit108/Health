package com.health.check.Controller;

import com.health.check.Dto.PatientDto;
import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Service.PatientService;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Patient;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@RestController
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Operation(summary = "Get Patient By Id")
    @GetMapping("/patients/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) throws NotFoundException {
        Patient patient = patientService.getPatientById(id);
        if(Objects.isNull(patient)) {
            throw new NotFoundException("Patient not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(patient);
    }

    @Operation(summary = "Get Logged In Patient Profile")
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patients/profile")
    public ResponseEntity<?> getPatientProfile() throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(patientService.getPatientByEmail(email));
    }

    @Operation(summary = "Update Logged In Patient Details")
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/patients")
    public ResponseEntity<?> updatePatient(@Valid @RequestBody PatientDto req) throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(patientService.updatePatient(email, req));
    }

    @Operation(summary = "Delete Logged In Patient")
    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/patients")
    public ResponseEntity<String> deletePatient() throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        PatientProfileResponse patientProfileResponse = patientService.getPatientByEmail(email);
        Patient patient = patientProfileResponse.getPatient();
        patientService.deletePatient(patient.getId());
        return ResponseEntity.ok("Patient deleted successfully");
    }
}
