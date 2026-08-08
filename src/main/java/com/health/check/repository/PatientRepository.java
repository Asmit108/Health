package com.health.check.repository;

import com.health.check.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient getPatientById(Long id);

    Patient getPatientByUserId(Long userId);
}
