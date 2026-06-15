package com.health.check.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SymptomReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private String possibleCauses;
    private String severity;
    private String remedies;
    private String whenToSeekCare;
    private String recommendedTests;
    private String lifeStyleTips;
    private String typeOfDoctorToSeek;
}
