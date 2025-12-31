package com.health.check.Service;

import com.health.check.Repository.AppointmentRepository;
import com.health.check.models.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    private final String INITIAL_STATUS = "SCHEDULED";

    public void createAppointment(Appointment req)
    {
        Appointment appointment = new Appointment();
        appointment.setDoctorId(req.getDoctorId());
        appointment.setAppointmentDate(req.getAppointmentDate());
        appointment.setPatientId(req.getPatientId());
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(INITIAL_STATUS));
    }

    public void updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(status));
        appointmentRepository.save(appointment);
    }

    public void reschedule(Long id, String date) {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        appointment.setAppointmentDate(date);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.getAppointmentsByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatientId(Long doctorId) {
        return appointmentRepository.getAppointmentsByPatientId(doctorId);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(Math.toIntExact(id));
    }
}
