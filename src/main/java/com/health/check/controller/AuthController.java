package com.health.check.controller;

import com.health.check.configuration.JwtProvider;
import com.health.check.dto.AuthResponse;
import com.health.check.dto.RegisterRequestDto;
import com.health.check.repository.DoctorRepository;
import com.health.check.repository.PatientRepository;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.models.Doctor;
import com.health.check.models.Patient;
import com.health.check.models.User;
import com.health.check.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final DoctorRepository doctorRepository;

    private final PatientRepository patientRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    public AuthController(UserRepository userRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, BCryptPasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Operation(summary = "Register User")
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequestDto registerRequest) throws Exception {
        // Check if user already exists
        User isExist = userRepository.findByEmail(registerRequest.getEmail());
        if (isExist != null) {
            throw new AlreadyExistsException("User already exists");
        }

        // Create new user entity with provided information
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        // Encrypt password before storing
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Parse and set role (normalize to lowercase)
        String role = registerRequest.getRole().trim().toLowerCase();
        if ("patient".equals(role)) {
            newUser.setRole(User.Role.PATIENT);
        } else {
            newUser.setRole(User.Role.DOCTOR);
        }

        // Save user to database
        User savedUser = userRepository.save(newUser);

        // Create role-specific record
        if (savedUser.getRole() == User.Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUserId(savedUser.getId());
            doctorRepository.save(doctor);
        } else {
            Patient patient = new Patient();
            patient.setUserId(savedUser.getId());
            patientRepository.save(patient);
        }

        // Generate JWT token with user role
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + savedUser.getRole().toString())
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), null, authorities);
        String token = jwtProvider.generateToken(authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, "register success", newUser.getRole().toString()));
    }

    @Operation(summary = "Login User")
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody RegisterRequestDto loginRequest) {
        // Authenticate user credentials
        Authentication authentication = authenticate(loginRequest);
        // Generate JWT token
        String token = jwtProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(token, "login success", user.getRole().toString()));
    }

    private Authentication authenticate(RegisterRequestDto loginRequest) {
        // Find user by email
        User userDetails = userRepository.findByEmail(loginRequest.getEmail());
        if (userDetails == null) {
            throw new BadCredentialsException("User not found...");
        }

        // Verify password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("wrong password...");
        }

        // Create authentication with user role
        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + userDetails.getRole().toString()));

        return new UsernamePasswordAuthenticationToken(userDetails.getEmail(), null, authorities);
    }
}