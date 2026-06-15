package com.health.check.Controller;

import com.health.check.Dto.RegisterRequestDto;
import com.health.check.Repository.DoctorRepository;
import com.health.check.Repository.PatientRepository;
import com.health.check.Repository.UserRepository;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.models.Doctor;
import com.health.check.models.Patient;
import com.health.check.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void signupPatientSuccess() throws Exception {

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("patient@test.com");
        dto.setPassword("123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setRole("patient");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(null);

        when(passwordEncoder.encode("123"))
                .thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(dto.getEmail());
        savedUser.setRole(User.Role.PATIENT);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        try (MockedStatic<com.health.check.Configuration.JwtProvider> jwt =
                     mockStatic(com.health.check.Configuration.JwtProvider.class)) {

            jwt.when(() -> com.health.check.Configuration.JwtProvider.generateToken(any(Authentication.class)))
                    .thenReturn("token");

            ResponseEntity<?> response =
                    authController.createUser(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            verify(patientRepository).save(any(Patient.class));
            verify(doctorRepository, never()).save(any(Doctor.class));
        }
    }

    @Test
    void signupDoctorSuccess() throws Exception {

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("doctor@test.com");
        dto.setPassword("123");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setRole("doctor");

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(null);

        when(passwordEncoder.encode("123"))
                .thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail(dto.getEmail());
        savedUser.setRole(User.Role.DOCTOR);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        try (MockedStatic<com.health.check.Configuration.JwtProvider> jwt =
                     mockStatic(com.health.check.Configuration.JwtProvider.class)) {

            jwt.when(() -> com.health.check.Configuration.JwtProvider.generateToken(any(Authentication.class)))
                    .thenReturn("token");

            ResponseEntity<?> response =
                    authController.createUser(dto);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            verify(doctorRepository).save(any(Doctor.class));
            verify(patientRepository, never()).save(any(Patient.class));
        }
    }

    @Test
    void signupUserAlreadyExists() {

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("existing@test.com");

        User existing = new User();

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(existing);

        assertThrows(
                AlreadyExistsException.class,
                () -> authController.createUser(dto)
        );
    }

    @Test
    void signinSuccess() {

        User loginRequest = new User();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("123");

        User dbUser = new User();
        dbUser.setEmail("user@test.com");
        dbUser.setPassword("encoded");
        dbUser.setRole(User.Role.PATIENT);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(dbUser);

        when(passwordEncoder.matches("123", "encoded"))
                .thenReturn(true);

        try (MockedStatic<com.health.check.Configuration.JwtProvider> jwt =
                     mockStatic(com.health.check.Configuration.JwtProvider.class)) {

            jwt.when(() -> com.health.check.Configuration.JwtProvider.generateToken(any(Authentication.class)))
                    .thenReturn("token");

            ResponseEntity<?> response =
                    authController.signin(loginRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void signinUserNotFound() {

        User loginRequest = new User();
        loginRequest.setEmail("unknown@test.com");

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(null);

        assertThrows(
                BadCredentialsException.class,
                () -> authController.signin(loginRequest)
        );
    }

    @Test
    void signinWrongPassword() {

        User loginRequest = new User();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("wrong");

        User dbUser = new User();
        dbUser.setEmail("user@test.com");
        dbUser.setPassword("encoded");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(dbUser);

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authController.signin(loginRequest)
        );
    }
}
