package com.health.check.Controller;

import com.health.check.Dto.DoctorDto;
import com.health.check.Dto.DoctorProfileResponse;
import com.health.check.Service.DoctorService;
import com.health.check.models.Doctor;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getDoctorsSuccess() {

        List<Doctor> doctors = List.of(
                new Doctor(),
                new Doctor()
        );

        when(doctorService.getDoctors(
                "Cardiology",
                5,
                1000.0))
                .thenReturn(doctors);

        ResponseEntity<?> response =
                doctorController.getDoctors(
                        "Cardiology",
                        5,
                        1000.0
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(doctors, response.getBody());

        verify(doctorService)
                .getDoctors("Cardiology", 5, 1000.0);
    }

    @Test
    void getDoctorProfileSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.com",
                        null
                )
        );

        DoctorProfileResponse profileResponse =
                new DoctorProfileResponse();

        when(doctorService.getDoctorByEmail("doctor@test.com"))
                .thenReturn(profileResponse);

        ResponseEntity<?> response =
                doctorController.getDoctorProfile();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileResponse, response.getBody());

        verify(doctorService)
                .getDoctorByEmail("doctor@test.com");
    }

    @Test
    void getDoctorByIdSuccess() throws Exception {

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(doctorService.getDoctorById(1L))
                .thenReturn(doctor);

        ResponseEntity<?> response =
                doctorController.getDoctorById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(doctor, response.getBody());

        verify(doctorService)
                .getDoctorById(1L);
    }

    @Test
    void updateDoctorSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.com",
                        null
                )
        );

        DoctorDto request = new DoctorDto();

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(1L);

        when(doctorService.updateDoctor(
                "doctor@test.com",
                request))
                .thenReturn(updatedDoctor);

        ResponseEntity<?> response =
                doctorController.updateDoctor(
                        request
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDoctor, response.getBody());

        verify(doctorService)
                .updateDoctor("doctor@test.com", request);
    }

    @Test
    void deleteDoctorSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.com",
                        null
                )
        );

        Doctor doctor = new Doctor();
        doctor.setId(10L);

        DoctorProfileResponse profileResponse =
                new DoctorProfileResponse();

        profileResponse.setDoctor(doctor);

        when(doctorService.getDoctorByEmail("doctor@test.com"))
                .thenReturn(profileResponse);

        ResponseEntity<?> response =
                doctorController.deleteDoctor();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "Doctor deleted successfully",
                response.getBody()
        );

        verify(doctorService)
                .getDoctorByEmail("doctor@test.com");

        verify(doctorService)
                .deleteDoctor(10L);
    }
}
