package com.health.check.Repository;

import com.health.check.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor getDoctorById(Long id);
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialisation AND d.experienceYears = :experience AND d.consultationFee = :fee")
    List<Doctor> getDoctors(@Param("specialisation") String specialisation,
                            @Param("experience") int experience,
                            @Param("fee") Double fee);

}
