package com.health.check.service;

import com.health.check.dto.DoctorDto;
import com.health.check.dto.DoctorProfileResponse;
import com.health.check.repository.DoctorRepository;
import com.health.check.repository.UserRepository;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Doctor;
import com.health.check.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing doctor-related business logic.
 * Handles doctor profile operations including search, retrieval, updates, and deletion.
 * Coordinates between DoctorRepository, UserRepository, and security components.
 *
 * @author Health Check Team
 * @version 1.0
 */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Doctor> getDoctors(String specialisation, Integer experience, Double maxFee) {
        return doctorRepository.getDoctors(specialisation, experience, maxFee);
    }

    public DoctorProfileResponse getDoctorByEmail(String email) throws NotFoundException {
        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        // Retrieve associated doctor record
        Doctor doctor = doctorRepository.getDoctorByUserId(user.getId());
        if (doctor == null) {
            throw new NotFoundException("Doctor not found");
        }
        return new DoctorProfileResponse(user, doctor);
    }

    public Doctor getDoctorById(Long id) throws NotFoundException {
        Doctor doctor = doctorRepository.getDoctorById(id);
        if (doctor == null) {
            throw new NotFoundException("Doctor not found");
        }
        return doctor;
    }

    public Doctor updateDoctor(String email, DoctorDto req) throws NotFoundException {
        DoctorProfileResponse doctorProfileResponse = getDoctorByEmail(email);
        Doctor doctor = doctorProfileResponse.getDoctor();
        if (req == null) {
            return doctor;
        }
        // Update doctor-specific fields if provided
        if (req.getSpecialization() != null) {
            doctor.setSpecialization(req.getSpecialization());
        }
        if (req.getClinicAddress() != null) {
            doctor.setClinicAddress(req.getClinicAddress());
        }
        if (req.getConsultationFee() != null) {
            doctor.setConsultationFee(req.getConsultationFee());
        }
        if (req.getExperienceYears() != 0) {
            doctor.setExperienceYears(req.getExperienceYears());
        }
        User user = doctorProfileResponse.getUser();

        // Update user profile fields if provided
        if (req.getAge() != null) {
            user.setAge(req.getAge());
        }
        if (req.getFirstName() != null) {
            user.setFirstName(req.getFirstName());
        }
        if (req.getLastName() != null) {
            user.setLastName(req.getLastName());
        }
        if (req.getSex() != null) {
            user.setSex(req.getSex());
        }
        // Encrypt password before saving
        if (req.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userRepository.save(user);
        doctorRepository.save(doctor);
        return doctor;
    }

    public void deleteDoctor(Long id) throws NotFoundException {
        Doctor doctor = getDoctorById(id);
        User user = userRepository.getUserById(doctor.getUserId());
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        // Delete both doctor and associated user records
        userRepository.deleteById(doctor.getUserId());
        doctorRepository.deleteById(id);
    }

}
