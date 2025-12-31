package com.health.check.Controller;

import com.health.check.Service.AppointmentService;
import com.health.check.models.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Create appointment
    @PostMapping
    public ResponseEntity<String> createAppointment(@RequestBody Appointment req) {
        appointmentService.createAppointment(req);
        return ResponseEntity.ok("Appointment created successfully");
    }

    // Update status
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestBody String status) {
        appointmentService.updateStatus(id,status);
        return ResponseEntity.ok("Appointment status updated successfully");
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<String> reschedule(@PathVariable Long id, @RequestBody String date) {
        appointmentService.reschedule(id,date);
        return ResponseEntity.ok("Appointment date updated successfully");
    }

    // Get all appointments for a doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("appointment deleted successfully");
    }
    //patch to update some field and put for all field
    // but we can use put for everything,,no issue

}
