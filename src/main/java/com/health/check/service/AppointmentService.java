package com.health.check.service;

import com.health.check.dto.PatientProfileResponse;
import com.health.check.models.Patient;
import com.health.check.repository.AppointmentRepository;
import com.health.check.dto.AppointmentRequestDto;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final PatientService patientService;

    public AppointmentService(AppointmentRepository appointmentRepository,  PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
    }

    public Appointment createAppointment(AppointmentRequestDto req) throws AlreadyExistsException {
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
        String initialStatus = "SCHEDULED";
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(initialStatus));
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

    public void deleteAppointment(Long id, String email) throws NotFoundException {
        PatientProfileResponse patientProfileResponse = patientService.getPatientByEmail(email);
        Patient patient = patientProfileResponse.getPatient();
        Appointment appointment = appointmentRepository.getAppointmentById(id);
        if (appointment == null) {
            throw new NotFoundException("appointment not found");
        }
        if(!Objects.equals(appointment.getPatientId(), patient.getId())){
            throw new AccessDeniedException("patient is not allowed to delete this appointment");
        }
        appointmentRepository.deleteById(Math.toIntExact(id));
    }
}
