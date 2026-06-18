package com.health.check.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private String firstName;
    private String lastName;
    private Integer age;
    private String sex;
    private String password;
}
