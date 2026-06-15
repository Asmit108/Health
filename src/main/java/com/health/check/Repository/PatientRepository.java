package com.health.check.Repository;

import com.health.check.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient getPatientById(Long id);

    @Query("SELECT p from Patient p WHERE p.user_id = :userId")
    Patient getPatientByUserId(Long userId);
}
