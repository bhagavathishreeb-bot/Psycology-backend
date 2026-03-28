package com.psycology.backend.repository;

import com.psycology.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByAppointmentDateAndAppointmentSlotStartAndPaymentStatusNot(
            LocalDate appointmentDate,
            LocalTime appointmentSlotStart,
            String paymentStatus);

    @Query("SELECT b.appointmentSlotStart FROM Booking b WHERE b.appointmentDate = :date AND b.appointmentSlotStart IS NOT NULL AND b.paymentStatus <> :failed")
    List<LocalTime> findTakenSlotStartsOn(@Param("date") LocalDate date, @Param("failed") String failed);
}
