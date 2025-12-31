package com.health.check.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Appointment {

    @Id
    private Long id;
    private Long doctorId;
    private Long patientId;
    private String appointmentDate;
    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED,
        REJECTED,
        CANCELLED
    }
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}
