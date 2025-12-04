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

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDto req) {

        // check if user exists
        if (userRepository.findByEmail(req.getEmail()) != null) {
            return "User already exists";
        }

        // create user object
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        // create fake Authentication for JWT generator
        Authentication auth = new UsernamePasswordAuthenticationToken(req.getEmail(), null);

        String jwt = JwtProvider.generateToken(auth);

        return jwt;     // return token to frontend
    }
}
