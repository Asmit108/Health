package com.health.check.models;

import jakarta.persistence.*;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialization;
    private int experienceYears;
    private Double consultationFee;
    private String clinicAddress;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
