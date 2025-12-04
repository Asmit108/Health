package com.health.check.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Doctor extends User {
    private String specialization;
    private int experienceYears;
    private Double consultationFee;  // doctor fee
    private String clinicAddress;
}
