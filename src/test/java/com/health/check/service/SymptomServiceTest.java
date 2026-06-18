package com.health.check.service;

import com.health.check.dto.SymptomResponseDto;
import com.health.check.models.Patient;
import com.health.check.repository.SymptomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SymptomServiceTest {

    @InjectMocks
    private SymptomService symptomService;

    @Mock
    private SymptomRepository symptomRepository;

    @Test
    void createSymptoms_shouldExecuteWithoutException() {

        Patient patient = new Patient();
        patient.setId(1L);

        SymptomResponseDto dto = new SymptomResponseDto();
        dto.setPossibleCauses("Flu");
        dto.setSeverity("Mild");
        dto.setRemedies("Rest");
        dto.setWhenToSeekCare("If worsens");
        dto.setRecommendedTests("Blood test");
        dto.setLifeStyleTips("Drink water");
        dto.setTypeOfDoctorToSeek("General physician");

        assertDoesNotThrow(() ->
                symptomService.createSymptoms(patient, dto)
        );
    }
}