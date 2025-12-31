package com.health.check.Service;

import com.health.check.Repository.DoctorRepository;
import com.health.check.Repository.UserRepository;
import com.health.check.models.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Doctor> getDoctors(String specialisation, int experience, Double fee)
    {
        return doctorRepository.getDoctors(specialisation,experience,fee);
    }

    public Doctor getDoctorById(Long Id)
    {
        return doctorRepository.getDoctorById(Id);
    }

    public void updateDoctor(Long Id, Doctor req)
    {
        if(req == null){
            return;
        }
        Doctor doctor = doctorRepository.getDoctorById(Id);
        if(req.getSpecialization() != null){
            doctor.setSpecialization(req.getSpecialization());
        }
        if(req.getClinicAddress() != null){
            doctor.setClinicAddress(req.getClinicAddress());
        }
        if(req.getConsultationFee() != null){
            doctor.setConsultationFee(req.getConsultationFee());
        }
        if(req.getExperienceYears() != 0){
            doctor.setExperienceYears(req.getExperienceYears());
        }
        doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long Id)
    {
        doctorRepository.deleteById(Id);
    }

}
