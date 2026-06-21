package com.health.check.service;

import com.health.check.exceptions.NotFoundException;
import com.health.check.models.User;
import com.health.check.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) throws NotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    public User getUserById(Long id) throws NotFoundException {
        User user = userRepository.getUserById(id);
        if(user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }
}
