package com.health.check.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomRequestDto {
    @NotBlank(message = "symptom is required")
    private String symptoms;
}
