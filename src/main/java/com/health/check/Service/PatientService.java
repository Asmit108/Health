package com.health.check.Service;

import com.health.check.Repository.PatientRepository;
import com.health.check.models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    PatientRepository patientRepository;

    public Patient getPatientById(Long id) {
        return patientRepository.getPatientById(id);
    }

    public void updatePatient(Long id, Patient req) {
        Patient patient = patientRepository.getPatientById(id);
        patient.getSymptomReports().addAll(req.getSymptomReports());
        patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

}
