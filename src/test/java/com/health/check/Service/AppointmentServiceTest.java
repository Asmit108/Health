package com.health.check.Service;

import com.health.check.Repository.AppointmentRepository;
import com.health.check.exceptions.AlreadyExistsException;
import com.health.check.exceptions.NotFoundException;
import com.health.check.models.Appointment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createAppointmentSuccess() throws Exception {

        Appointment request = new Appointment();
        request.setDoctorId(1L);
        request.setPatientId(2L);
        request.setAppointmentDateTime(LocalDateTime.now());

        when(appointmentRepository.getAppointmentsByAppointmentDateTime(
                request.getAppointmentDateTime()))
                .thenReturn(List.of());

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result =
                appointmentService.createAppointment(request);

        assertNotNull(result);
        assertEquals(1L, result.getDoctorId());
        assertEquals(2L, result.getPatientId());

        assertEquals(
                Appointment.AppointmentStatus.SCHEDULED,
                result.getStatus()
        );

        verify(appointmentRepository)
                .save(any(Appointment.class));
    }

    @Test
    void createAppointmentAlreadyBooked() {

        Appointment request = new Appointment();
        request.setAppointmentDateTime(LocalDateTime.now());

        when(appointmentRepository.getAppointmentsByAppointmentDateTime(
                request.getAppointmentDateTime()))
                .thenReturn(List.of(new Appointment()));

        AlreadyExistsException exception =
                assertThrows(
                        AlreadyExistsException.class,
                        () -> appointmentService.createAppointment(request)
                );

        assertEquals(
                "Time is already booked",
                exception.getMessage()
        );

        verify(appointmentRepository, never())
                .save(any());
    }

    @Test
    void updateStatusSuccess() throws Exception {

        Appointment appointment = new Appointment();

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        appointmentService.updateStatus(
                1L,
                "COMPLETED"
        );

        assertEquals(
                Appointment.AppointmentStatus.COMPLETED,
                appointment.getStatus()
        );

        verify(appointmentRepository)
                .save(appointment);
    }

    @Test
    void updateStatusAppointmentNotFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> appointmentService.updateStatus(
                                1L,
                                "COMPLETED"
                        )
                );

        assertEquals(
                "appointment not found",
                exception.getMessage()
        );
    }

    @Test
    void updateStatusInvalidStatus() {

        Appointment appointment = new Appointment();

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.updateStatus(
                        1L,
                        "INVALID_STATUS"
                )
        );
    }

    @Test
    void rescheduleSuccess() throws Exception {

        Appointment appointment = new Appointment();

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        LocalDateTime newDate =
                LocalDateTime.now().plusDays(1);

        appointmentService.reschedule(
                1L,
                newDate
        );

        assertEquals(
                newDate,
                appointment.getAppointmentDateTime()
        );

        verify(appointmentRepository)
                .save(appointment);
    }

    @Test
    void rescheduleAppointmentNotFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> appointmentService.reschedule(
                                1L,
                                LocalDateTime.now()
                        )
                );

        assertEquals(
                "appointment not found",
                exception.getMessage()
        );
    }

    @Test
    void getAppointmentsByDoctorIdSuccess() {

        List<Appointment> appointments =
                List.of(new Appointment());

        when(appointmentRepository.getAppointmentsByDoctorId(1L))
                .thenReturn(appointments);

        List<Appointment> result =
                appointmentService.getAppointmentsByDoctorId(1L);

        assertEquals(appointments, result);
    }

    @Test
    void getAppointmentsByPatientIdSuccess() {

        List<Appointment> appointments =
                List.of(new Appointment());

        when(appointmentRepository.getAppointmentsByPatientId(1L))
                .thenReturn(appointments);

        List<Appointment> result =
                appointmentService.getAppointmentsByPatientId(1L);

        assertEquals(appointments, result);
    }

    @Test
    void deleteAppointmentSuccess() throws Exception {

        Appointment appointment = new Appointment();

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(appointment);

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository)
                .deleteById(1);
    }

    @Test
    void deleteAppointmentNotFound() {

        when(appointmentRepository.getAppointmentById(1L))
                .thenReturn(null);

        NotFoundException exception =
                assertThrows(
                        NotFoundException.class,
                        () -> appointmentService.deleteAppointment(1L)
                );

        assertEquals(
                "appointment not found",
                exception.getMessage()
        );
    }
}
