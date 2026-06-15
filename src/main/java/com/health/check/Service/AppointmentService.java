package com.health.check.Service;

import com.health.check.Repository.AppointmentRepository;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment createAppointment(Appointment req) throws AlreadyExistsException {
        Appointment appointment = new Appointment();
        appointment.setDoctorId(req.getDoctorId());
        LocalDateTime dateTime = req.getAppointmentDateTime();

        // Check if the time slot is already booked
        List<Appointment> appointments = appointmentRepository.getAppointmentsByAppointmentDateTime(dateTime);
        if (!appointments.isEmpty()) {
            throw new AlreadyExistsException("Time is already booked");
        }

        appointment.setAppointmentDateTime(req.getAppointmentDateTime());
        appointment.setPatientId(req.getPatientId());

        // Set initial status to SCHEDULED
        String INITIAL_STATUS = "SCHEDULED";
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(INITIAL_STATUS));
        return appointmentRepository.save(appointment);
    }

    public void updateStatus(Long id, String status) throws NotFoundException {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        if (appointment == null) {
            throw new NotFoundException("appointment not found");
        }
        // Convert string status to enum and update
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(status));
        appointmentRepository.save(appointment);
    }

    public void reschedule(Long id, LocalDateTime dateTime) throws NotFoundException {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        if (appointment == null) {
            throw new NotFoundException("appointment not found");
        }
        appointment.setAppointmentDateTime(dateTime);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.getAppointmentsByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.getAppointmentsByPatientId(patientId);
    }

    public void deleteAppointment(Long id) throws NotFoundException {
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        if (appointment == null) {
            throw new NotFoundException("appointment not found");
        }
        appointmentRepository.deleteById(Math.toIntExact(id));
    }
}
