package com.health.check.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "doctorId is required")
    private Long doctorId;
    @NotBlank(message = "patientId is required")
    private Long patientId;
    @NotBlank(message = "DateTime is required")
    private LocalDateTime appointmentDateTime;
    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED,
        REJECTED,
        CANCELLED,
        COMPLETED
    }
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}
