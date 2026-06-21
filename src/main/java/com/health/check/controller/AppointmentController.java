package com.health.check.controller;

import com.health.check.dto.AppointmentRequestDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.service.AppointmentService;
import com.health.check.service.PatientService;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import com.health.check.models.Patient;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    private final PatientService patientService;

    public AppointmentController(AppointmentService appointmentService, PatientService patientService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @Operation(summary = "Create Appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequestDto req) throws NotFoundException, AlreadyExistsException {
        // Extract email from authentication token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        PatientProfileResponse patientProfileResponse = patientService.getPatientByEmail(email);
        Patient patient = patientProfileResponse.getPatient();
        // Automatically set patient ID from authenticated user
        req.setPatientId(patient.getId());
        Appointment response = appointmentService.createAppointment(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update Status of Appointment")
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) throws NotFoundException {
        // Extract email from authentication token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        appointmentService.updateStatus(id, status, email);
        return ResponseEntity.ok("Appointment status updated successfully");
    }

    @Operation(summary = "Reschedule Appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<String> reschedule(@PathVariable Long id, @RequestParam LocalDateTime dateTime) throws NotFoundException {
        // Extract email from authentication token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        appointmentService.reschedule(id, dateTime, email);
        return ResponseEntity.ok("Appointment date updated successfully");
    }

    @Operation(summary = "Get Appointments by Doctor Id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    @Operation(summary = "Get Appointments by Patient Id")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    @Operation(summary = "Delete Appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) throws NotFoundException {
        // Extract email from authentication token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        appointmentService.deleteAppointment(id, email);
        return ResponseEntity.ok("appointment deleted successfully");
    }
}
