package com.health.check.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long doctorId;
    private Long patientId;
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
