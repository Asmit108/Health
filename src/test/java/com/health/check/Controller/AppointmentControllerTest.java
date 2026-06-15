package com.health.check.Controller;

import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Service.AppointmentService;
import com.health.check.Service.PatientService;
import com.health.check.models.Appointment;
import com.health.check.models.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private AppointmentController appointmentController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createAppointmentSuccess() throws Exception {

        Patient patient = new Patient();
        patient.setId(1L);

        PatientProfileResponse profileResponse =
                new PatientProfileResponse();
        profileResponse.setPatient(patient);

        Appointment request = new Appointment();
        Appointment saved = new Appointment();
        saved.setId(100L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@gmail.com",
                        null
                )
        );

        when(patientService.getPatientByEmail("test@gmail.com"))
                .thenReturn(profileResponse);

        when(appointmentService.createAppointment(any(Appointment.class)))
                .thenReturn(saved);

        ResponseEntity<?> response =
                appointmentController.createAppointment(
                        request
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
        assertEquals(1L, request.getPatientId());

        verify(patientService).getPatientByEmail("test@gmail.com");
        verify(appointmentService).createAppointment(request);
    }

    @Test
    void updateStatusSuccess() throws Exception {

        ResponseEntity<?> response =
                appointmentController.updateStatus(
                        1L,
                        "CONFIRMED"
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "Appointment status updated successfully",
                response.getBody()
        );

        verify(appointmentService)
                .updateStatus(1L, "CONFIRMED");
    }

    @Test
    void rescheduleSuccess() throws Exception {

        LocalDateTime dateTime = LocalDateTime.now();

        ResponseEntity<?> response =
                appointmentController.reschedule(
                        1L,
                        dateTime
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "Appointment date updated successfully",
                response.getBody()
        );

        verify(appointmentService)
                .reschedule(1L, dateTime);
    }

    @Test
    void getAppointmentsByDoctorIdSuccess() {

        List<Appointment> appointments = List.of(
                new Appointment(),
                new Appointment()
        );

        when(appointmentService.getAppointmentsByDoctorId(1L))
                .thenReturn(appointments);

        ResponseEntity<?> response =
                appointmentController.getAppointmentsByDoctorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());

        verify(appointmentService)
                .getAppointmentsByDoctorId(1L);
    }

    @Test
    void getAppointmentsByPatientIdSuccess() {

        List<Appointment> appointments = List.of(
                new Appointment()
        );

        when(appointmentService.getAppointmentsByPatientId(1L))
                .thenReturn(appointments);

        ResponseEntity<?> response =
                appointmentController.getAppointmentsByPatientId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());

        verify(appointmentService)
                .getAppointmentsByPatientId(1L);
    }

    @Test
    void deleteAppointmentSuccess() throws Exception {

        ResponseEntity<?> response =
                appointmentController.deleteAppointment(
                        1L
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "appointment deleted successfully",
                response.getBody()
        );

        verify(appointmentService)
                .deleteAppointment(1L);
    }

}
