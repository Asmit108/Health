package com.health.check.Controller;

import com.health.check.Configuration.JwtProvider;
import com.health.check.Dto.AuthResponse;
import com.health.check.Dto.RegisterRequestDto;
import com.health.check.Repository.DoctorRepository;
import com.health.check.models.Doctor;
import com.health.check.models.User;
import com.health.check.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUser(@RequestBody RegisterRequestDto registerRequest) throws Exception {
        User isExist = userRepository.findByEmail(registerRequest.getEmail());
        if (isExist != null) {
            throw new Exception("Email already used with another account");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        String role = registerRequest.getRole().trim().toLowerCase();
        switch (role) {
            case "patient":
                newUser.setRole(User.Role.PATIENT);
                break;
            case "doctor":
                newUser.setRole(User.Role.DOCTOR);
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + registerRequest.getRole());
        }

        User savedUser = userRepository.save(newUser);
        if(savedUser.getRole() == User.Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.user = savedUser;
            doctorRepository.save(doctor);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser, savedUser);
        String token = JwtProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token,"register success",newUser.getRole().toString()));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) throws Exception {
        Authentication authentication = authenticate(loginRequest);
        String token = JwtProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new Exception("User not found");
        }

        return ResponseEntity.ok(new AuthResponse(token,"login success",user.getRole().toString()));
    }

    private Authentication authenticate(User loginRequest) throws Exception {

        User userDetails = userRepository.findByEmail(loginRequest.getEmail());
        if (userDetails == null) {
            throw new BadCredentialsException("invalid username...");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("wrong password...");
        }
        Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + userDetails.getRole()));

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}