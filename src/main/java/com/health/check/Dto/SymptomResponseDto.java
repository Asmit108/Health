package com.health.check.Dto;

import lombok.Data;

@Data
public class SymptomResponseDto {
    private String possibleCauses;
    private String severity;
    private String remedies;
    private String whenToSeekCare;
    private String recommendedTests;
    private String lifeStyleTips;
    private String typeOfDoctorToSeek;
}
