package com.health.check.controller;

import com.health.check.dto.PatientDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.models.User;
import com.health.check.service.PatientService;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Patient;
import com.health.check.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientController patientController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getPatientProfileByIdSuccess() throws Exception {

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUserId(99L);

        when(patientService.getPatientById(1L))
                .thenReturn(patient);

        User user = new User();
        user.setId(99L);
        user.setEmail("patient@test.com");

        when(userService.getUserById(99L))
                .thenReturn(user);

        ResponseEntity<PatientProfileResponse> response =
                patientController.getPatientProfileById(1L);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
        assertEquals(patient, response.getBody().getPatient());
        assertEquals(user, response.getBody().getUser());

        verify(patientService)
                .getPatientById(1L);
        verify(userService)
                .getUserById(99L);
    }

    @Test
    void getPatientProfileByIdNotFound() {

        when(patientService.getPatientById(1L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> patientController.getPatientProfileById(1L)
        );

        verify(patientService)
                .getPatientById(1L);
    }

    @Test
    void getPatientProfileSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );

        PatientProfileResponse profileResponse =
                new PatientProfileResponse();

        when(patientService.getPatientByEmail(
                "patient@test.com"))
                .thenReturn(profileResponse);

        ResponseEntity<PatientProfileResponse> response =
                patientController.getPatientProfile();

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(profileResponse, response.getBody());

        verify(patientService)
                .getPatientByEmail(
                        "patient@test.com"
                );
    }

    @Test
    void updatePatientProfileSuccess() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );
        PatientDto request = new PatientDto();

        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);

        PatientProfileResponse patientProfileResponse = new PatientProfileResponse();
        patientProfileResponse.setPatient(updatedPatient);

        when(patientService.updatePatient(
                "patient@test.com",
                request))
                .thenReturn(patientProfileResponse);

        ResponseEntity<PatientProfileResponse> response =
                patientController.updatePatientProfile(
                        request
                );

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(patientProfileResponse, response.getBody());

        verify(patientService)
                .updatePatient(
                        "patient@test.com",
                        request
                );
    }

    @Test
    void deletePatientSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "patient@test.com",
                        null
                )
        );

        Patient patient = new Patient();
        patient.setId(10L);

        PatientProfileResponse profileResponse =
                new PatientProfileResponse();
        profileResponse.setPatient(patient);

        when(patientService.getPatientByEmail(
                "patient@test.com"))
                .thenReturn(profileResponse);

        ResponseEntity<String> response =
                patientController.deletePatient();

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                "Patient deleted successfully",
                response.getBody()
        );

        verify(patientService)
                .getPatientByEmail(
                        "patient@test.com"
                );

        verify(patientService)
                .deletePatient(10L);
    }
}
