package com.health.check.controller;

import com.health.check.dto.AppointmentRequestDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.models.Appointment;
import com.health.check.models.Patient;
import com.health.check.service.AppointmentService;
import com.health.check.service.PatientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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

    private void mockSecurityContext(String email) {
        Authentication auth =
                new UsernamePasswordAuthenticationToken(email, null);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void createAppointment_success() throws Exception {

        mockSecurityContext("test@gmail.com");

        Patient patient = new Patient();
        patient.setId(1L);

        PatientProfileResponse profile = new PatientProfileResponse();
        profile.setPatient(patient);

        AppointmentRequestDto dto = new AppointmentRequestDto();

        Appointment appointment = new Appointment();

        when(patientService.getPatientByEmail("test@gmail.com"))
                .thenReturn(profile);

        when(appointmentService.createAppointment(dto))
                .thenReturn(appointment);

        ResponseEntity<Appointment> response =
                appointmentController.createAppointment(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1L, dto.getPatientId());

        verify(appointmentService).createAppointment(dto);
    }

    @Test
    void updateStatus_success() throws Exception {

        mockSecurityContext("doc@gmail.com");

        doNothing().when(appointmentService)
                .updateStatus(1L, "APPROVED", "doc@gmail.com");

        ResponseEntity<String> response =
                appointmentController.updateStatus(1L, "APPROVED");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("updated"));

        verify(appointmentService).updateStatus(1L, "APPROVED", "doc@gmail.com");
    }

    @Test
    void reschedule_success() throws Exception {

        mockSecurityContext("test@gmail.com");

        LocalDateTime time = LocalDateTime.now();

        doNothing().when(appointmentService)
                .reschedule(1L, time, "test@gmail.com");

        ResponseEntity<String> response =
                appointmentController.reschedule(1L, time);

        assertEquals(200, response.getStatusCodeValue());
        verify(appointmentService).reschedule(1L, time, "test@gmail.com");
    }

    @Test
    void getAppointmentsByDoctorId_success() {

        List<Appointment> list = List.of(new Appointment());

        when(appointmentService.getAppointmentsByDoctorId(1L))
                .thenReturn(list);

        ResponseEntity<List<Appointment>> response =
                appointmentController.getAppointmentsByDoctorId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAppointmentsByPatientId_success() {

        List<Appointment> list = List.of(new Appointment());

        when(appointmentService.getAppointmentsByPatientId(1L))
                .thenReturn(list);

        ResponseEntity<List<Appointment>> response =
                appointmentController.getAppointmentsByPatientId(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void deleteAppointment_success() throws Exception {

        mockSecurityContext("test@gmail.com");

        doNothing().when(appointmentService)
                .deleteAppointment(1L, "test@gmail.com");

        ResponseEntity<String> response =
                appointmentController.deleteAppointment(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("deleted"));

        verify(appointmentService).deleteAppointment(1L, "test@gmail.com");
    }
}