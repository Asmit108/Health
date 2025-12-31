package com.health.check.Controller;

import com.health.check.Service.DoctorService;
import com.health.check.Service.PatientService;
import com.health.check.models.Doctor;
import com.health.check.models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    // ✅ Update Doctor
    @PutMapping("/patients/{id}")
    public ResponseEntity<String> updatePatient(@PathVariable Long id, @RequestBody Patient req) {
        patientService.updatePatient(id, req);
        return ResponseEntity.ok("Patient updated successfully");
    }

    // ✅ Delete Doctor
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok("Patient deleted successfully");
    }
}
