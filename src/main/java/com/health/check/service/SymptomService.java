package com.health.check.service;

import com.health.check.dto.SymptomResponseDto;
import com.health.check.repository.SymptomRepository;
import com.health.check.models.Patient;
import com.health.check.models.SymptomReport;
import org.springframework.stereotype.Service;

@Service
public class SymptomService {

    private final SymptomRepository symptomRepository;

    public SymptomService(SymptomRepository symptomRepository) {
        this.symptomRepository = symptomRepository;
    }

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
