package com.health.check.service;

import com.health.check.dto.DoctorDto;
import com.health.check.dto.DoctorProfileResponse;
import com.health.check.repository.DoctorRepository;
import com.health.check.repository.UserRepository;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Doctor;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void getDoctorsTest() {
        when(doctorRepository.getDoctors(null, null, null))
                .thenReturn(java.util.List.of(new Doctor()));

        assertEquals(1, doctorService.getDoctors(null, null, null).size());
    }

    @Test
    void getDoctorByEmail_UserNotFound() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> doctorService.getDoctorByEmail("a@b.com"));
    }

    @Test
    void getDoctorByEmail_DoctorNotFound() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(doctorRepository.getDoctorByUserId(1L)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> doctorService.getDoctorByEmail("a@b.com"));
    }

    @Test
    void getDoctorByEmail_Success() throws Exception {
        User user = new User();
        user.setId(1L);

        Doctor doctor = new Doctor();

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(doctorRepository.getDoctorByUserId(1L)).thenReturn(doctor);

        DoctorProfileResponse response =
                doctorService.getDoctorByEmail("a@b.com");

        assertNotNull(response);
    }

    @Test
    void getDoctorById_NotFound() {
        when(doctorRepository.getDoctorById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> doctorService.getDoctorById(1L));
    }

    @Test
    void getDoctorById_Success() throws Exception {
        Doctor doctor = new Doctor();

        when(doctorRepository.getDoctorById(1L)).thenReturn(doctor);

        assertEquals(doctor, doctorService.getDoctorById(1L));
    }

    @Test
    void updateDoctor_NullRequest() throws Exception {

        User user = new User();
        user.setId(1L);

        Doctor doctor = new Doctor();

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(doctorRepository.getDoctorByUserId(1L)).thenReturn(doctor);

        Doctor result = doctorService.updateDoctor("a@b.com", null);

        assertEquals(doctor, result);
    }

    @Test
    void updateDoctor_AllFields() throws Exception {

        User user = new User();
        user.setId(1L);

        Doctor doctor = new Doctor();

        DoctorDto dto = new DoctorDto();
        dto.setSpecialization("Cardio");
        dto.setClinicAddress("Hyd");
        dto.setConsultationFee(500.0);
        dto.setExperienceYears(5);
        dto.setAge(30);
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setSex("M");
        dto.setPassword("pass");

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(doctorRepository.getDoctorByUserId(1L)).thenReturn(doctor);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        Doctor result = doctorService.updateDoctor("a@b.com", dto);

        assertEquals(doctor, result);

        verify(userRepository).save(user);
        verify(doctorRepository).save(doctor);

        assertEquals("Cardio", doctor.getSpecialization());
        assertEquals("Hyd", doctor.getClinicAddress());
        assertEquals(500.0, doctor.getConsultationFee());
        assertEquals(5, doctor.getExperienceYears());

        assertEquals(30, user.getAge());
        assertEquals("A", user.getFirstName());
        assertEquals("B", user.getLastName());
        assertEquals("M", user.getSex());
        assertEquals("encoded", user.getPassword());
    }

    @Test
    void updateDoctor_NullFields() throws Exception {

        User user = new User();
        user.setId(1L);

        Doctor doctor = new Doctor();

        DoctorDto dto = new DoctorDto();

        when(userRepository.findByEmail("a@b.com")).thenReturn(user);
        when(doctorRepository.getDoctorByUserId(1L)).thenReturn(doctor);

        Doctor result = doctorService.updateDoctor("a@b.com", dto);

        assertEquals(doctor, result);

        verify(userRepository).save(user);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void deleteDoctor_DoctorNotFound() {
        when(doctorRepository.getDoctorById(1L)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> doctorService.deleteDoctor(1L));
    }

    @Test
    void deleteDoctor_UserNotFound(){
        Doctor doctor = new Doctor();
        doctor.setUserId(10L);

        when(doctorRepository.getDoctorById(1L)).thenReturn(doctor);
        when(userRepository.getUserById(10L)).thenReturn(null);

        assertThrows(NotFoundException.class,
                () -> doctorService.deleteDoctor(1L));
    }

    @Test
    void deleteDoctor_Success() throws Exception {
        Doctor doctor = new Doctor();
        doctor.setUserId(10L);

        User user = new User();

        when(doctorRepository.getDoctorById(1L)).thenReturn(doctor);
        when(userRepository.getUserById(10L)).thenReturn(user);

        doctorService.deleteDoctor(1L);

        verify(userRepository).deleteById(10L);
        verify(doctorRepository).deleteById(1L);
    }
}