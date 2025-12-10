package com.health.check.Controller;

import com.health.check.Configuration.JwtProvider;
import com.health.check.Dto.AuthResponse;
import com.health.check.Dto.RegisterRequestDto;
import com.health.check.models.User;
import com.health.check.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public AuthResponse createUser(@RequestBody User user) throws Exception {
        User isExist = userRepository.findByEmail(user.getEmail());
        if (isExist != null) {
            throw new Exception("Email already used with another account");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(User.Role.PATIENT);
        User savedUser = userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser, savedUser);
        String token = JwtProvider.generateToken(authentication);
        AuthResponse res = new AuthResponse(token, "register success", savedUser.getRole().toString());
        return res;
    }

    @PostMapping("/signin")
    public AuthResponse signin(@RequestBody User loginRequest) throws Exception {
        Authentication authentication = authenticate(loginRequest);
        String token = JwtProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new Exception("User not found");
        }

        AuthResponse res = new AuthResponse(token, "login success", user.getRole().toString());
        return res;
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