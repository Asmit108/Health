package com.health.check.Dto;

import com.health.check.models.Patient;
import com.health.check.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientProfileResponse {
    private User user;
    private Patient patient;
}
