package com.health.check.Service;

import com.health.check.Dto.SymptomResponseDto;
import com.health.check.Repository.SymptomRepository;
import com.health.check.models.Patient;
import com.health.check.models.SymptomReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SymptomService {

    @Autowired
    private SymptomRepository symptomRepository;

    public void createSymptoms(Patient patient, SymptomResponseDto symptomResponseDto) {
        // Create new symptom report entity
        SymptomReport symptomReport = new SymptomReport();

        // Associate report with patient
        symptomReport.setPatientId(patient.getId());

        // Populate symptom report with AI analysis data
        symptomReport.setPossibleCauses(symptomResponseDto.getPossibleCauses());
        symptomReport.setSeverity(symptomResponseDto.getSeverity());
        symptomReport.setRemedies(symptomResponseDto.getRemedies());
        symptomReport.setWhenToSeekCare(symptomResponseDto.getWhenToSeekCare());
        symptomReport.setRecommendedTests(symptomResponseDto.getRecommendedTests());
        symptomReport.setLifeStyleTips(symptomResponseDto.getLifeStyleTips());
        symptomReport.setTypeOfDoctorToSeek(symptomResponseDto.getTypeOfDoctorToSeek());

        symptomRepository.save(symptomReport);
    }

}
