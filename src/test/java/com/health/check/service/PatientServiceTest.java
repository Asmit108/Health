package com.health.check.service;

import com.health.check.dto.PatientDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Patient;
import com.health.check.models.User;
import com.health.check.repository.PatientRepository;
import com.health.check.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    @Test
    void getPatientById_success() {
        Patient patient = new Patient();

        when(patientRepository.getPatientById(1L))
                .thenReturn(patient);

        Patient result = patientService.getPatientById(1L);

        assertEquals(patient, result);
    }

    @Test
    void getPatientByEmail_success() throws Exception {

        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(user);

        when(patientRepository.getPatientByUserId(1L))
                .thenReturn(patient);

        PatientProfileResponse response =
                patientService.getPatientByEmail("test@gmail.com");

        assertNotNull(response);
        assertEquals(user, response.getUser());
        assertEquals(patient, response.getPatient());
    }

    @Test
    void getPatientByEmail_userNotFound() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> patientService.getPatientByEmail("test@gmail.com")
        );
    }

    @Test
    void getPatientByEmail_patientNotFound() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(user);

        when(patientRepository.getPatientByUserId(1L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> patientService.getPatientByEmail("test@gmail.com")
        );
    }

    @Test
    void updatePatient_nullRequest_shouldReturnPatient() throws Exception {

        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();
        patient.setUserId(1L);

        PatientProfileResponse patientProfileResponse = new PatientProfileResponse();
        patientProfileResponse.setUser(user);
        patientProfileResponse.setPatient(patient);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(user);

        when(patientRepository.getPatientByUserId(1L))
                .thenReturn(patient);

        PatientProfileResponse result =
                patientService.updatePatient("test@gmail.com", null);

        assertEquals(patientProfileResponse, result);
    }

    @Test
    void updatePatient_success() throws Exception {

        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();
        patient.setUserId(1L);

        PatientProfileResponse patientProfileResponse = new PatientProfileResponse();
        patientProfileResponse.setUser(user);
        patientProfileResponse.setPatient(patient);

        PatientDto dto = new PatientDto();
        dto.setAge(25);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setSex("Male");
        dto.setPassword("password");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(user);

        when(patientRepository.getPatientByUserId(1L))
                .thenReturn(patient);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        PatientProfileResponse result =
                patientService.updatePatient("test@gmail.com", dto);

        assertEquals(patientProfileResponse, result);

        verify(userRepository).save(user);

        assertEquals(25, user.getAge());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Male", user.getSex());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void deletePatient_success() throws Exception {

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUserId(10L);

        User user = new User();
        user.setId(10L);

        when(patientRepository.getPatientById(1L))
                .thenReturn(patient);

        when(userRepository.getUserById(10L))
                .thenReturn(user);

        patientService.deletePatient(1L);

        verify(userRepository).deleteById(10L);
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void deletePatient_userNotFound() {

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUserId(10L);

        when(patientRepository.getPatientById(1L))
                .thenReturn(patient);

        when(userRepository.getUserById(10L))
                .thenReturn(null);

        assertThrows(
                NotFoundException.class,
                () -> patientService.deletePatient(1L)
        );
    }
}