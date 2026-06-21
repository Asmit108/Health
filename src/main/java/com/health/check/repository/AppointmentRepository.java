package com.health.check.repository;

import com.health.check.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment getAppointmentById(Long id);

    List<Appointment> getAppointmentsByDoctorId(Long doctorId);

    List<Appointment> getAppointmentsByPatientId(Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime = :dateTime AND a.doctorId = :doctorId")
    List<Appointment> getAppointmentsByAppointmentDateTimeAndDoctorId(@Param("dateTime") LocalDateTime dateTime,
                                                                      @Param("doctorId") Long doctorId);
}
