package com.health.check.Service;

import com.health.check.Repository.DoctorRepository;
import com.health.check.models.Doctor;
import org.springframework.beans.factory.annotation.Autowired;

public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public void createDoctor(Doctor doctor)
    {

    }
}
