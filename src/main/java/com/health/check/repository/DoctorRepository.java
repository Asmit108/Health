package com.health.check.repository;

import com.health.check.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor getDoctorById(Long id);

    @Query("SELECT d FROM Doctor d WHERE (:specialization IS NULL OR d.specialization = :specialization) AND (:experience IS NULL OR d.experienceYears = :experience) AND (:maxFee IS NULL OR d.consultationFee >= :maxFee)")
    List<Doctor> getDoctors(@Param("specialization") String specialization,
                            @Param("experience") Integer experience,
                            @Param("maxFee") Double maxFee);

    Doctor getDoctorByUserId(Long userId);
}
