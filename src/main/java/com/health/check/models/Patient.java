package com.health.check.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Patient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @ElementCollection
    private List<String> symptomReports;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
