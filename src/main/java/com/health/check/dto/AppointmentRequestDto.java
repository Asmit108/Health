package com.health.check.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {
    @NotBlank(message = "doctorId is required")
    private Long doctorId;
    @NotBlank(message = "patientId is required")
    private Long patientId;
    @NotBlank(message = "DateTime is required")
    private LocalDateTime appointmentDateTime;
}
