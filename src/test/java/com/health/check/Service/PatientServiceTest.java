package com.health.check.Service;

import com.health.check.Dto.PatientDto;
import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Repository.PatientRepository;
import com.health.check.Repository.UserRepository;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Patient;
import com.health.check.models.User;
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
    void getPatientByIdTest() {
        Patient patient = new Patient();

        when(patientRepository.getPatientById(1L)).thenReturn(patient);

        Patient result = patientService.getPatientById(1L);

        assertEquals(patient, result);
    }

    @Test
    void getPatientByEmail_UserNull() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> patientService.getPatientByEmail("a@b.com"));
    }

    @Test
    void getPatientByEmail_PatientNull() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(patientRepository.getPatientByUserId(1L)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> patientService.getPatientByEmail("a@b.com"));
    }

    @Test
    void getPatientByEmail_Success() throws Exception {
        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(patientRepository.getPatientByUserId(1L)).thenReturn(patient);

        PatientProfileResponse result =
                patientService.getPatientByEmail("a@b.com");

        assertNotNull(result);
    }

    @Test
    void updatePatient_NullRequest() throws Exception {

        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(patientRepository.getPatientByUserId(1L)).thenReturn(patient);

        Patient result = patientService.updatePatient("a@b.com", null);

        assertEquals(patient, result);
    }

    @Test
    void updatePatient_AllFields() throws Exception {

        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();

        PatientDto dto = new PatientDto();
        dto.setAge(25);
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setSex("M");
        dto.setPassword("pass");

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(patientRepository.getPatientByUserId(1L)).thenReturn(patient);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        Patient result = patientService.updatePatient("a@b.com", dto);

        assertEquals(patient, result);

        assertEquals(25, user.getAge());
        assertEquals("A", user.getFirstName());
        assertEquals("B", user.getLastName());
        assertEquals("M", user.getSex());
        assertEquals("encoded", user.getPassword());

        verify(userRepository).save(user);
    }

    @Test
    void updatePatient_NullFields() throws Exception {

        User user = new User();
        user.setId(1L);

        Patient patient = new Patient();

        PatientDto dto = new PatientDto();
        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(patientRepository.getPatientByUserId(1L)).thenReturn(patient);

        Patient result = patientService.updatePatient("a@b.com", dto);

        assertEquals(patient, result);

        verify(userRepository).save(user);
    }

    @Test
    void deletePatient_NotFound() {
        when(patientRepository.getPatientById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> patientService.deletePatient(1L));
    }

    @Test
    void deletePatient_Success() throws NotFoundException {

        Patient patient = new Patient();

        when(patientRepository.getPatientById(1L)).thenReturn(patient);

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
    }
}