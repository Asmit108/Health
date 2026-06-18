package com.health.check.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Patient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
}
