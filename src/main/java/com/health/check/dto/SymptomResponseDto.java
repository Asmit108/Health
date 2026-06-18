package com.health.check.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomResponseDto {
    private String possibleCauses;
    private String severity;
    private String remedies;
    private String whenToSeekCare;
    private String recommendedTests;
    private String lifeStyleTips;
    private String typeOfDoctorToSeek;
}
