package com.health.check.models;

import jakarta.persistence.*;

import java.util.List;

public class Patient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String symptomReport1;
    private String symptomReport2;
    private String symptomReport3;
    private String symptomReport4;
    private String symptomReport5;
    private String symptomReport6;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
