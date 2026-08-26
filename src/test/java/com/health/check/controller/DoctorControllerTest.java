package com.health.check.controller;

import com.health.check.dto.DoctorDto;
import com.health.check.dto.DoctorProfileResponse;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.User;
import com.health.check.service.DoctorService;
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
    void getDoctorProfilesSuccess() throws NotFoundException {

        List<DoctorProfileResponse> doctors = List.of(
                new DoctorProfileResponse(),
                new DoctorProfileResponse()
        );

        when(doctorService.getDoctors(
                "Cardiology",
                5,
                1000.0))
                .thenReturn(doctors);

        ResponseEntity<?> response =
                doctorController.getDoctorProfiles(
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
    void getDoctorProfileByIdSuccess() throws Exception {

        DoctorProfileResponse doctorProfileResponse = new DoctorProfileResponse();
        doctorProfileResponse.setUser(new User());
        doctorProfileResponse.setDoctor(new  Doctor());

        when(doctorService.getDoctorById(1L))
                .thenReturn(doctorProfileResponse);

        ResponseEntity<?> response =
                doctorController.getDoctorProfileById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(doctorProfileResponse, response.getBody());

        verify(doctorService)
                .getDoctorById(1L);
    }

    @Test
    void updateDoctorProfileSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "doctor@test.com",
                        null
                )
        );

        DoctorDto request = new DoctorDto();

        DoctorProfileResponse updatedDoctorProfileResponse = new DoctorProfileResponse();
        updatedDoctorProfileResponse.setDoctor(new Doctor());
        updatedDoctorProfileResponse.getDoctor().setId(1L);

        when(doctorService.updateDoctor(
                "doctor@test.com",
                request))
                .thenReturn(updatedDoctorProfileResponse);

        ResponseEntity<?> response =
                doctorController.updateDoctorProfile(
                        request
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedDoctorProfileResponse, response.getBody());

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
