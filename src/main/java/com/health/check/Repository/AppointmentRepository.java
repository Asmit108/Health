package com.health.check.Repository;

import com.health.check.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    public Appointment getAppointmentById(Long id);

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId);

    public List<Appointment> getAppointmentsByPatientId(Long patientId);
}
