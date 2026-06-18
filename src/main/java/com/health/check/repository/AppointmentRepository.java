package com.health.check.repository;

import com.health.check.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    Appointment getAppointmentById(Long id);

    List<Appointment> getAppointmentsByDoctorId(Long doctorId);

    List<Appointment> getAppointmentsByPatientId(Long patientId);

    List<Appointment> getAppointmentsByAppointmentDateTime(LocalDateTime dateTime);
}
