package com.health.check.Controller;

import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Service.AppointmentService;
import com.health.check.Service.PatientService;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import com.health.check.models.Patient;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Operation(summary = "Create Appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointment req) throws NotFoundException, AlreadyExistsException {
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
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) throws NotFoundException {
        appointmentService.updateStatus(id, status);
        return ResponseEntity.ok("Appointment status updated successfully");
    }

    @Operation(summary = "Reschedule Appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> reschedule(@PathVariable Long id, @RequestParam LocalDateTime dateTime) throws NotFoundException {
        appointmentService.reschedule(id, dateTime);
        return ResponseEntity.ok("Appointment date updated successfully");
    }

    @Operation(summary = "Get Appointments by Doctor Id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    @Operation(summary = "Get Appointments by Patient Id")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    @Operation(summary = "Delete Appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) throws NotFoundException {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("appointment deleted successfully");
    }
}
