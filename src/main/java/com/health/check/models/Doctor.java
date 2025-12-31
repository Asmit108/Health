package com.health.check.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialization;
    private int experienceYears;
    private Double consultationFee;
    private String clinicAddress;
    @OneToOne(cascade = CascadeType.ALL)
    public User user;
}
