package com.health.check.Dto;

import com.health.check.models.Doctor;
import com.health.check.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorProfileResponse {
    private User user;
    private Doctor doctor;
}
