package com.health.check.service;

import com.health.check.dto.PatientDto;
import com.health.check.dto.PatientProfileResponse;
import com.health.check.models.Doctor;
import com.health.check.repository.PatientRepository;
import com.health.check.repository.UserRepository;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Patient;
import com.health.check.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Patient getPatientById(Long id) {
        return patientRepository.getPatientById(id);
    }

    public PatientProfileResponse getPatientByEmail(String email) throws NotFoundException {
        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        // Retrieve associated patient record
        Patient patient = patientRepository.getPatientByUserId(user.getId());
        if (patient == null) {
            throw new NotFoundException("Patient not found");
        }
        return new PatientProfileResponse(user, patient);
    }

    public PatientProfileResponse updatePatient(String email, PatientDto req) throws NotFoundException {
        PatientProfileResponse patientProfileResponse = getPatientByEmail(email);
        if (req == null) {
            return patientProfileResponse;
        }
        User user = patientProfileResponse.getUser();

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
        patientProfileResponse.setUser(user);
        return patientProfileResponse;
    }

    public void deletePatient(Long id) throws NotFoundException {
        Patient patient = getPatientById(id);
        User user = userRepository.getUserById(patient.getUserId());
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        // Delete both doctor and associated user records
        userRepository.deleteById(patient.getUserId());
        patientRepository.deleteById(id);
    }

}
