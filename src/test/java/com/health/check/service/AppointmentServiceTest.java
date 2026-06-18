package com.health.check.service;

import com.health.check.dto.AppointmentRequestDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import com.health.check.models.Patient;
import com.health.check.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createAppointment_success() throws Exception {

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setDoctorId(1L);
        dto.setPatientId(2L);
        dto.setAppointmentDateTime(LocalDateTime.now());

        when(appointmentRepository
                .getAppointmentsByAppointmentDateTime(dto.getAppointmentDateTime()))
                .thenReturn(List.of());

        Appointment saved = new Appointment();
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(saved);

        Appointment result = appointmentService.createAppointment(dto);

        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_alreadyBooked() {

        AppointmentRequestDto dto = new AppointmentRequestDto();
        dto.setAppointmentDateTime(LocalDateTime.now());

        when(appointmentRepository
                .getAppointmentsByAppointmentDateTime(dto.getAppointmentDateTime()))
                .thenReturn(List.of(new Appointment()));

        assertThrows(
                AlreadyExistsException.class,
                () -> appointmentService.createAppointment(dto)
        );
    }

    @Test
    void updateStatus_success() throws Exception {

        Appointment appointment = new Appointment();

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        appointmentService.updateStatus(1L, "SCHEDULED");

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void updateStatus_notFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> appointmentService.updateStatus(1L, "SCHEDULED")
        );
    }

    @Test
    void reschedule_success() throws Exception {

        Appointment appointment = new Appointment();

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        appointmentService.reschedule(1L, LocalDateTime.now());

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void reschedule_notFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> appointmentService.reschedule(1L, LocalDateTime.now())
        );
    }

    @Test
    void getAppointmentsByDoctorId_success() {

        when(appointmentRepository.getAppointmentsByDoctorId(1L))
                .thenReturn(List.of(new Appointment()));

        List<Appointment> result =
                appointmentService.getAppointmentsByDoctorId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getAppointmentsByPatientId_success() {

        when(appointmentRepository.getAppointmentsByPatientId(1L))
                .thenReturn(List.of(new Appointment()));

        List<Appointment> result =
                appointmentService.getAppointmentsByPatientId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void deleteAppointment_success() throws Exception {

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse profile = new PatientProfileResponse();
        profile.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setPatientId(10L);

        when(patientService.getPatientByEmail("test@gmail.com"))
                .thenReturn(profile);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        appointmentService.deleteAppointment(1L, "test@gmail.com");

        verify(appointmentRepository).deleteById(1);
    }

    @Test
    void deleteAppointment_notFound() throws NotFoundException {

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse profile = new PatientProfileResponse();
        profile.setPatient(patient);

        when(patientService.getPatientByEmail("test@gmail.com"))
                .thenReturn(profile);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> appointmentService.deleteAppointment(1L, "test@gmail.com")
        );
    }

    @Test
    void deleteAppointment_accessDenied() throws NotFoundException {

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse profile = new PatientProfileResponse();
        profile.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setPatientId(20L);

        when(patientService.getPatientByEmail("test@gmail.com"))
                .thenReturn(profile);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        assertThrows(
                AccessDeniedException.class,
                () -> appointmentService.deleteAppointment(1L, "test@gmail.com")
        );
    }
}