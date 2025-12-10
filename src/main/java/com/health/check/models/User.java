package com.health.check.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String sex;
    private String email;
    private String password;
    // 👇 ENUM role (patient or doctor)
    public enum Role {
        PATIENT,
        DOCTOR
    }
    @Enumerated(EnumType.STRING)
    private Role role;
}
