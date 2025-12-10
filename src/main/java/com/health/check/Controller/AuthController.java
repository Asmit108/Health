package com.health.check.Controller;

import com.health.check.Configuration.JwtProvider;
import com.health.check.Dto.RegisterRequestDto;
import com.health.check.models.User;
import com.health.check.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public AuthResponse createUser(@RequestBody User user) throws Exception {
        User isExist=userRepository.findByEmail(user.getEmail());
        if(isExist!=null){
            throw new Exception("Email already used with another account");
        }

        User newUser=new User();
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole("USER");
        User savedUser=userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser, savedUser);
        String token= JwtProvider.