package com.health.check.service;

import com.health.check.dto.DoctorProfileResponse;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import com.health.check.models.Doctor;
import com.health.check.models.Patient;
import com.health.check.repository.AppointmentRepository;
import com.health.check.dto.AppointmentRequestDto;
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

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private AppointmentService appointmentService;

    // ---------------- CREATE ----------------

    @Test
    void createAppointment_success() throws Exception {

        AppointmentRequestDto req = new AppointmentRequestDto();
        req.setDoctorId(1L);
        req.setPatientId(2L);
        req.setAppointmentDateTime(LocalDateTime.now());

        when(doctorService.getDoctorById(1L)).thenReturn(new Doctor());

        when(appointmentRepository
                .getAppointmentsByAppointmentDateTimeAndDoctorId(any(), eq(1L)))
                .thenReturn(List.of());

        when(appointmentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.createAppointment(req);

        assertNotNull(result);
        assertEquals(1L, result.getDoctorId());
        assertEquals(2L, result.getPatientId());
    }

    @Test
    void createAppointment_timeSlotBooked() throws Exception {

        AppointmentRequestDto req = new AppointmentRequestDto();
        req.setDoctorId(1L);
        req.setAppointmentDateTime(LocalDateTime.now());

        when(doctorService.getDoctorById(1L)).thenReturn(new Doctor());

        when(appointmentRepository
                .getAppointmentsByAppointmentDateTimeAndDoctorId(any(), eq(1L)))
                .thenReturn(List.of(new Appointment()));

        assertThrows(AlreadyExistsException.class,
                () -> appointmentService.createAppointment(req));
    }

    // ---------------- UPDATE STATUS ----------------

    @Test
    void updateStatus_success() throws Exception {

        Appointment appointment = new Appointment();
        appointment.setDoctorId(10L);

        Doctor doctor = new Doctor();
        doctor.setId(10L);

        DoctorProfileResponse response = new DoctorProfileResponse();
        response.setDoctor(doctor);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        when(doctorService.getDoctorByEmail("doc@gmail.com"))
                .thenReturn(response);

        appointmentService.updateStatus(1L, "SCHEDULED", "doc@gmail.com");

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void updateStatus_notFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> appointmentService.updateStatus(1L, "SCHEDULED", "email"));
    }

    @Test
    void updateStatus_accessDenied() throws NotFoundException {

        Appointment appointment = new Appointment();
        appointment.setDoctorId(99L);

        Doctor doctor = new Doctor();
        doctor.setId(10L);

        DoctorProfileResponse response = new DoctorProfileResponse();
        response.setDoctor(doctor);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        when(doctorService.getDoctorByEmail("doc@gmail.com"))
                .thenReturn(response);

        assertThrows(AccessDeniedException.class,
                () -> appointmentService.updateStatus(1L, "SCHEDULED", "doc@gmail.com"));
    }

    // ---------------- RESCHEDULE ----------------

    @Test
    void reschedule_success() throws Exception {

        Appointment appointment = new Appointment();
        appointment.setPatientId(1L);

        Patient patient = new Patient();
        patient.setId(1L);

        PatientProfileResponse response = new PatientProfileResponse();
        response.setPatient(patient);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        when(patientService.getPatientByEmail("pat@gmail.com"))
                .thenReturn(response);

        appointmentService.reschedule(1L, LocalDateTime.now(), "pat@gmail.com");

        verify(appointmentRepository).save(appointment);
    }

    @Test
    void reschedule_notFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> appointmentService.reschedule(1L, LocalDateTime.now(), "email"));
    }

    @Test
    void reschedule_accessDenied() throws NotFoundException {

        Appointment appointment = new Appointment();
        appointment.setPatientId(99L);

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse response = new PatientProfileResponse();
        response.setPatient(patient);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        when(patientService.getPatientByEmail("pat@gmail.com"))
                .thenReturn(response);

        assertThrows(AccessDeniedException.class,
                () -> appointmentService.reschedule(1L, LocalDateTime.now(), "pat@gmail.com"));
    }

    // ---------------- DELETE ----------------

    @Test
    void delete_success() throws Exception {

        Appointment appointment = new Appointment();
        appointment.setPatientId(1L);

        Patient patient = new Patient();
        patient.setId(1L);

        PatientProfileResponse response = new PatientProfileResponse();
        response.setPatient(patient);

        when(patientService.getPatientByEmail("pat@gmail.com"))
                .thenReturn(response);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        appointmentService.deleteAppointment(1L, "pat@gmail.com");

        verify(appointmentRepository).deleteById(1);
    }

    @Test
    void delete_notFound() throws NotFoundException {

        Patient patient = new Patient();
        patient.setId(1L);

        PatientProfileResponse response = new PatientProfileResponse();
        response.setPatient(patient);

        when(patientService.getPatientByEmail("pat@gmail.com"))
                .thenReturn(response);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> appointmentService.deleteAppointment(1L, "pat@gmail.com"));
    }

    @Test
    void delete_accessDenied() throws NotFoundException {

        Appointment appointment = new Appointment();
        appointment.setPatientId(99L);

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse response = new PatientProfileResponse();
        response.setPatient(patient);

        when(patientService.getPatientByEmail("pat@gmail.com"))
                .thenReturn(response);

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        assertThrows(AccessDeniedException.class,
                () -> appointmentService.deleteAppointment(1L, "pat@gmail.com"));
    }

    // ---------------- GET METHODS ----------------

    @Test
    void getByDoctor_success() {

        when(appointmentRepository.getAppointmentsByDoctorId(1L))
                .thenReturn(List.of(new Appointment()));

        assertEquals(1,
                appointmentService.getAppointmentsByDoctorId(1L).size());
    }

    @Test
    void getByPatient_success() {

        when(appointmentRepository.getAppointmentsByPatientId(1L))
                .thenReturn(List.of(new Appointment()));

        assertEquals(1,
                appointmentService.getAppointmentsByPatientId(1L).size());
    }
}