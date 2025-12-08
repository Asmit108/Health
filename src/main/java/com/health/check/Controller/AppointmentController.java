package com.example.demo.controller;

import com.example.demo.dto.CreateAppointmentRequest;
import com.example.demo.dto.UpdateAppointmentStatusRequest;
import com.example.demo.dto.AppointmentResponse;
import com.example.demo.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Create appointment
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody CreateAppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.createAppointment(request));
    }

    // Update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentStatusRequest request) {

        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, request));
    }

    // Get all appointments for a doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctorId(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }
}
