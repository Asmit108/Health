package com.health.check.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {
    private String specialization;
    private int experienceYears;
    private Double consultationFee;
    private String clinicAddress;
    private String firstName;
    private String lastName;
    private Integer age;
    private String sex;
    private String password;
}
