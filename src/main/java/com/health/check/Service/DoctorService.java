package com.health.check.Service;

import com.health.check.Repository.DoctorRepository;
import com.health.check.Repository.UserRepository;
import com.health.check.models.Doctor;
import com.health.check.models.User;
import org.springframework.beans.factory.annotation.Autowired;

public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

}
