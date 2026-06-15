package com.health.check.Service;

import com.health.check.Dto.PatientDto;
import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Repository.PatientRepository;
import com.health.check.Repository.UserRepository;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Patient;
import com.health.check.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Patient getPatientById(Long id) {
        return patientRepository.getPatientById(id);
    }

    public PatientProfileResponse getPatientByEmail(String email) throws NotFoundException {
        // Find user by email
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new NotFoundException("User not found");
        }
        // Retrieve associated patient record
        Patient patient = patientRepository.getPatientByUserId(user.getId());
        if(patient == null) {
            throw new NotFoundException("Patient not found");
        }
        return new PatientProfileResponse(user, patient);
    }

    public Patient updatePatient(String email, PatientDto req) throws NotFoundException {
        PatientProfileResponse patientProfileResponse = getPatientByEmail(email);
        Patient patient = patientProfileResponse.getPatient();
        if(req == null) {
            return patient;
        }
        User user = patientProfileResponse.getUser();

        // Update user profile fields if provided
        if(req.getAge() != null){
            user.setAge(req.getAge());
        }
        if(req.getFirstName() != null){
            user.setFirstName(req.getFirstName());
        }
        if(req.getLastName() != null){
            user.setLastName(req.getLastName());
        }
        if(req.getSex() != null){
            user.setSex(req.getSex());
        }
        // Encrypt password before saving
        if(req.getPassword() != null){
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        userRepository.save(user);
        return patient;
    }

    public void deletePatient(Long id) throws NotFoundException {
        Patient patient = patientRepository.getPatientById(id);
        if(patient == null) {
            throw new NotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

}
